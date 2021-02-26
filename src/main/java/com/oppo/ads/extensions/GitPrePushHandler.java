package com.oppo.ads.extensions;

import com.intellij.dvcs.push.PrePushHandler;
import com.intellij.dvcs.push.PushInfo;
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
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;
import com.intellij.vcs.log.VcsFullCommitDetails;
import com.oppo.ads.persistent.SettingPersistent;
import com.oppo.ads.utils.ProjectUtil;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author 80275131
 * @version 1.0
 * @date 2021/2/18 17:15
 * @since 1.0
 **/
public class GitPrePushHandler implements PrePushHandler {
    private static final Logger LOG = Logger.getInstance(GitPrePushHandler.class);
    private static List<String> referencePaths = new ArrayList<>();
    @Nls(capitalization = Nls.Capitalization.Title)
    @NotNull
    @Override
    public String getPresentableName() {
        return "将变更导出到yapi";
    }

    @NotNull
    @Override
    public Result handle(@NotNull List<PushInfo> list, @NotNull ProgressIndicator progressIndicator) {
        boolean ifNeedExport = false;
        Project currentProject = ProjectUtil.getCurrentProject();
        SettingPersistent settingPersistent = SettingPersistent.getInstance(currentProject);
        if (!settingPersistent.gitPushSwitch) {
            LOG.info("gitPushSwitch is not open");
            return Result.OK;
        }
        String listenerDir = settingPersistent.listenDirs;
        String exportDir = settingPersistent.exportDirs;
        if (StringUtils.isAnyBlank(listenerDir, exportDir)) {
            LOG.info("auto api listenDirs or exportDirs not config");
            return Result.OK;
        }


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
                    System.out.println("获取文件路径" + file.getPath());
                    System.out.println("获取文件名称" + file.getName());

                    VirtualFile virtualFile = file.getVirtualFile();
                    PsiFile psiFile = PsiManager.getInstance(currentProject).findFile(virtualFile);
                    getReferenceClassPath(psiFile);


//                    FilePath filePath = new LocalFilePath(listenerDir, true);
//                    if (file.isUnder(filePath, false)) {
//                        ifNeedExport = true;
//                        break OUT;
//                    }
                }
            }
        }

        List<String> needExportPaths = getNeedExportPaths(listenerDir);
        export(needExportPaths,currentProject);
//        if (ifNeedExport) {
//            String[] split = exportDir.trim().split(",");
//            for (String s : split) {
////				YapiExporter.export(currentProject, s);
//                System.out.println("--------" + s);
//            }
//        }
        return Result.OK;
    }



    void getReferenceClassPath(PsiFile psiFile) {
        PsiElement child = psiFile.getChildren()[1];
        Query<PsiReference> search = ReferencesSearch.search(child);
        Collection<PsiReference> all = search.findAll();

        for (PsiReference psiReference : all) {
            /*使用api：psiReference.getElement()*/
            PsiElement element = psiReference.getElement();
            String text = element.getText();
            PsiFile containingFile = element.getContainingFile();
            String path = containingFile.getVirtualFile().getPath();

            if (!referencePaths.contains(path)) {

                referencePaths.add(path);
                /*递归查询*/
                getReferenceClassPath(containingFile);
            }
        }
        return;
    }

    List<String> getNeedExportPaths(String listenerDir){
        List<String> strings = new ArrayList<>();
        for(String path:referencePaths){
            if(path.contains(listenerDir)){
                strings.add(path);
            }
        }
        return strings;
    }

    void export(List<String> referencePaths,Project currentProject){
        for(String path:referencePaths){
            YapiExporter.export(currentProject, path);
        }
    }

}
