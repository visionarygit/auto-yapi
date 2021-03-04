package com.ulrica.idea.utils;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.concurrency.Promise;

/**
 * project工具类
 *
 * @author 80275131
 * @version 1.0
 * @date 2021/2/19 9:43
 * @since 1.0
 **/
public class ProjectUtil {

	public static Project getCurrentProject() {
		Promise<DataContext> promise = DataManager.getInstance().getDataContextFromFocusAsync();
		Project pro = null;
		try {
			DataContext dataContext = promise.blockingGet(1000);
			pro = dataContext.getData(CommonDataKeys.PROJECT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pro;
	}

	public static Project[] getAllOpenProjects() {
		return ProjectManager.getInstance().getOpenProjects();
	}
}
