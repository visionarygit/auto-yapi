package com.oppo.ads.extensions;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.itangcent.idea.extensionPoint.YapiExporterExtensionPoint;
import com.oppo.ads.utils.NotificationUtil;


/**
 *
 *
 * @author 80275131
 * @version 1.0
 * @date 2021/2/18 9:58
 * @since 1.0
 **/
public class YapiExporter {

	private static final ExtensionPointName<YapiExporterExtensionPoint> EP_NAME =
			ExtensionPointName.create("com.itangcent.idea.plugin.easy-yapi.yapiExporterExecutor");

	public static void export(Project project, String path) {
		YapiExporterExtensionPoint[] extensions = EP_NAME.getExtensions();
		for (YapiExporterExtensionPoint extension : extensions) {
			if (extension != null) {
				extension.doExport(project, path);
			}
		}
		NotificationUtil.notifyInfo("Auto Yapi", "自动导出完成");
	}

	public static void exportByPsiFile(Project project, PsiFile psiFile) {
		YapiExporterExtensionPoint[] extensions = EP_NAME.getExtensions();
		for (YapiExporterExtensionPoint extension : extensions) {
			if (extension != null) {
				extension.doExportByPsiFile(project,psiFile);
			}
		}
		NotificationUtil.notifyInfo("Auto Yapi", "自动导出完成");
	}

}
