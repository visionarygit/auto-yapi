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
import com.intellij.vcs.log.VcsFullCommitDetails;
import com.oppo.ads.persistent.SettingPersistent;
import com.oppo.ads.utils.ProjectUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * git推送事件执行器
 *
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

		OUT:
		for (PushInfo pushInfo : list) {
			List<VcsFullCommitDetails> commits = pushInfo.getCommits();
			for (VcsFullCommitDetails commit : commits) {
				Collection<Change> changes = commit.getChanges();
				for (Change change : changes) {
					ContentRevision afterRevision = change.getAfterRevision();
					FilePath file = afterRevision.getFile();
					FilePath filePath = new LocalFilePath(listenerDir, true);
					if (file.isUnder(filePath, false)) {
						ifNeedExport = true;
						break OUT;
					}
				}
			}
		}

		if (ifNeedExport) {
			String[] split = exportDir.trim().split(",");
			for (String s : split) {
				YapiExporter.export(currentProject, s);
			}
		}
		return Result.OK;
	}
}
