package com.ulrica.idea.extensions;

import com.intellij.dvcs.push.PrePushHandler;
import com.intellij.dvcs.push.PushInfo;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.LocalFilePath;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopes;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;
import com.intellij.vcs.log.VcsFullCommitDetails;
import com.ulrica.idea.configurable.PropertiesConfigurable;
import com.ulrica.idea.configurable.PropertiesDetail;
import com.ulrica.idea.configurable.SettingConfigurable;
import com.ulrica.idea.persistent.SettingPersistent;
import com.ulrica.idea.utils.ProjectUtil;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 80275131
 * @version 1.0
 * @date 2021/2/18 17:15
 * @since 1.0
 **/
public class GitPrePushHandler implements PrePushHandler {
    private static final Logger LOG = Logger.getInstance(GitPrePushHandler.class);

    @Nls(capitalization = Nls.Capitalization.Title)
    @NotNull
    @Override
    public String getPresentableName() {
        return "将变更导出到yapi";
    }


    private SettingPersistent getSettingPersistent(Project currentProject) {
        SettingPersistent instance = SettingPersistent.getInstance(currentProject);
        if (!instance.gitPushSwitch) {
            LOG.info("idea配置信息为空，将从配置文件中读取！");
            return null;
        }
        LOG.info("检测到idea中存在配置信息，优先采用idea配置，若要关闭，请取消勾选git push与时间任务");
        return instance;
    }

    private <T> Pair<String, String> getDirs(T t) {
        Pair<String, String> pair;
        if (t.getClass().equals(SettingPersistent.class)) {
            SettingPersistent settingPersistent = (SettingPersistent) t;
            pair = new Pair<>(settingPersistent.exportDirs, settingPersistent.listenDirs);
        } else {
            PropertiesDetail propertiesDetail = (PropertiesDetail) t;
            pair = new Pair<>(propertiesDetail.getExportDirs(), propertiesDetail.getListenDirs());
        }
        return pair;
    }

    @NotNull
    @Override
    public Result handle(@NotNull List<PushInfo> list, @NotNull ProgressIndicator progressIndicator) {
        Project currentProject = ProjectUtil.getCurrentProject();
        Pair<String, String> stringPair;
        SettingPersistent persistent = getSettingPersistent(currentProject);
        if (persistent != null) {
            stringPair = getDirs(Objects.requireNonNull(persistent));
        } else {
            stringPair = getDirs(Objects.requireNonNull(PropertiesConfigurable.readProperties(currentProject)));
        }
        String exportDir = stringPair.getKey();
        String listenerDir = stringPair.getValue();
        if (StringUtils.isAnyBlank(listenerDir, exportDir)) {
            LOG.info("auto api listenDirs or exportDirs not config");
            return Result.OK;
        }
        //定义检索范围
        GlobalSearchScope searchScope = ReadAction.compute(() -> {
            VirtualFile fileByPath = LocalFileSystem.getInstance().findFileByPath(listenerDir);
            if (fileByPath == null) {
                return null;
            }
            PsiDirectory directory = PsiManager.getInstance(currentProject).findDirectory(fileByPath);
            if (directory == null) {
                return null;
            }
            return GlobalSearchScopes.directoryScope(directory, true);
        });
        //获取引用文件
        List<PsiClass> referencePsiFile = new ArrayList<>();
        for (PushInfo pushInfo : list) {
            List<VcsFullCommitDetails> commits = pushInfo.getCommits();
            for (VcsFullCommitDetails commit : commits) {
                Collection<Change> changes = commit.getChanges();
                for (Change change : changes) {
                    ContentRevision afterRevision = change.getAfterRevision();
                    if (afterRevision == null) {
                        continue;
                    }
                    FilePath file = afterRevision.getFile();
                    VirtualFile virtualFile = file.getVirtualFile();
                    FilePath listenerPath = new LocalFilePath(listenerDir, true);
                    //不在监听目录下的文件不进行处理
                    if (!file.isUnder(listenerPath, false) || virtualFile == null) {
                        continue;
                    }
                    PsiFile psiFile = ReadAction.compute(() -> PsiManager.getInstance(currentProject).findFile(virtualFile));
                    getReferencePsiFile(psiFile, referencePsiFile, searchScope);
                }
            }
        }
        //引用过滤转化为psiClass
        String[] exportDirs = exportDir.split(",");
        List<PsiClass> psiClasses = referencePsiFile
                .stream()
                .filter(ref -> {
                    String path = ReadAction.compute(() -> ref.getContainingFile().getVirtualFile().getPath());
                    FilePath refFilePath = new LocalFilePath(path, false);
                    return Stream.of(exportDirs).anyMatch(dir -> refFilePath.isUnder(new LocalFilePath(dir, false), false));
                })
                .filter(PsiClass::isInterface)
                .collect(Collectors.toList());
        //批量导出
        if (psiClasses.size() > 0) {
            YapiExporter.exportByPsiFiles(currentProject, psiClasses);
        }
        return Result.OK;
    }


    void getReferencePsiFile(PsiFile psiFile, List<PsiClass> referencePsiFile, GlobalSearchScope globalSearchScope) {
        ReadAction.run(() -> {
            PsiClass child = PsiTreeUtil.findChildOfType(psiFile, PsiClass.class);
            if (child == null) {
                return;
            }
            if (!referencePsiFile.contains(child)) {
                referencePsiFile.add(child);
            } else {
                return;
            }
            Query<PsiReference> search = ReferencesSearch.search(child, globalSearchScope);
            Collection<PsiReference> all = search.findAll();
            for (PsiReference psiReference : all) {
                PsiElement element = psiReference.getElement();
                PsiFile containingFile = element.getContainingFile();
                /*递归查询*/
                getReferencePsiFile(containingFile, referencePsiFile, globalSearchScope);
            }
        });
    }

}
