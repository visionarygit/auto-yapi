package com.oppo.ads.extensions;

import com.intellij.dvcs.push.PrePushHandler;
import com.intellij.dvcs.push.PushInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import com.intellij.vcs.log.VcsFullCommitDetails;
import com.oppo.ads.persistent.SettingPersistent;
import com.oppo.ads.utils.FileUtil;
import com.oppo.ads.utils.ProjectUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
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

    @Nls(capitalization = Nls.Capitalization.Title)
    @NotNull
    @Override
    public String getPresentableName() {
        return "将变更导出到yapi";
    }

    @NotNull
    @Override
    public Result handle(@NotNull List<PushInfo> list, @NotNull ProgressIndicator progressIndicator) {
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

        List<PsiFile> referencePsiFile = new ArrayList<>();
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
                    String path = virtualFile.getPath();

                    PsiFile psiFile = PsiManager.getInstance(currentProject).findFile(virtualFile);
                    if (!referencePsiFile.contains(psiFile)) {
                        referencePsiFile.add(psiFile);
                    }
                    getReferencePsiFile(psiFile, referencePsiFile);
                }
            }
        }
        List<PsiFile> needExportPsiFile = getNeedExportPsiFile(listenerDir, referencePsiFile);
        export(needExportPsiFile, currentProject);

        return Result.OK;
    }


    void getReferencePsiFile(PsiFile psiFile, List<PsiFile> referencePsiFile) {
        PsiElement child = psiFile.getChildren()[1];
        Query<PsiReference> search = ReferencesSearch.search(child);
        Collection<PsiReference> all = search.findAll();

        for (PsiReference psiReference : all) {
            /*使用api：psiReference.getElement()*/
            PsiElement element = psiReference.getElement();
            String text = element.getText();
            PsiFile containingFile = element.getContainingFile();
            if (!referencePsiFile.contains(containingFile)) {
                referencePsiFile.add(containingFile);
                /*递归查询*/
                getReferencePsiFile(containingFile, referencePsiFile);
            }
        }
        return;
    }

    List<PsiFile> getNeedExportPsiFile(String listenerDir, List<PsiFile> referencePsiFile) {
        List<PsiFile> psiFiles = new ArrayList<>();
        for (PsiFile psiFile : referencePsiFile) {
            String path = psiFile.getVirtualFile().getPath();
            if (FileUtil.ifContains(path, listenerDir)) {
                psiFiles.add(psiFile);
            }
        }
        return psiFiles;
    }

    void export(List<PsiFile> needExportPsiFiles, Project currentProject) {
        for (PsiFile psiFile : needExportPsiFiles) {
            YapiExporter.exportByPsiFile(currentProject, psiFile);
        }
    }

}
