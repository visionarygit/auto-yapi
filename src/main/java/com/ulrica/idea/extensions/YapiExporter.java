package com.ulrica.idea.extensions;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.file.PsiDirectoryImpl;
import com.itangcent.idea.extensionPoint.YapiExporterExtensionPoint;
import com.ulrica.idea.utils.NotificationUtil;

import java.util.ArrayList;
import java.util.List;


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
		VirtualFile fileByPath = LocalFileSystem.getInstance().findFileByPath(path);
		if (fileByPath == null) {
			return;
		}
		PsiManagerImpl psiManager = new PsiManagerImpl(project);
		PsiDirectoryImpl psiDirectory = new PsiDirectoryImpl(psiManager, fileByPath);
		List<Navigatable> navigatables = new ArrayList<>();
		navigatables.add(psiDirectory);
		doExport(project, navigatables);
	}


	public static void exportByPsiFiles(Project currentProject, List<PsiClass> needExportPsiFiles) {
		doExport(currentProject, needExportPsiFiles);
	}

	private static void doExport(Project project, List<? extends Navigatable> navigatables) {
		YapiExporterExtensionPoint[] extensions = EP_NAME.getExtensions();
		for (YapiExporterExtensionPoint extension : extensions) {
			if (extension != null) {
				extension.doExport(project, navigatables);
			}
		}
		NotificationUtil.notifyInfo("Auto Yapi", "自动导出完成");
	}
}