package com.ulrica.idea.persistent;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 配置持久化
 *
 * @author 80275131
 * @version 1.0
 * @date 2021/2/18 20:28
 * @since 1.0
 **/
@State(name = "SettingPersistent", storages = {@Storage("AutoYapiPlugin.xml")})
public class SettingPersistent implements PersistentStateComponent<SettingPersistent> {

	public String listenDirs = "";
	public String exportDirs = "";
	public boolean gitPushSwitch = false;
	public boolean schedulePushSwitch = false;
	public String firstTime = "-1";
	public String intervalTime = "-1";


	public static SettingPersistent getInstance(Project project) {
		return ServiceManager.getService(project, SettingPersistent.class);
	}

	@Nullable
	@Override
	public SettingPersistent getState() {
		return this;
	}

	@Override
	public void loadState(@NotNull SettingPersistent settingPersistent) {
		XmlSerializerUtil.copyBean(settingPersistent, this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SettingPersistent that = (SettingPersistent) o;
		return gitPushSwitch == that.gitPushSwitch &&
				schedulePushSwitch == that.schedulePushSwitch &&
				Objects.equals(listenDirs, that.listenDirs) &&
				Objects.equals(exportDirs, that.exportDirs) &&
				Objects.equals(intervalTime, that.intervalTime) &&
				Objects.equals(firstTime, that.firstTime);
	}

	@Override
	public int hashCode() {
		return Objects.hash(listenDirs, exportDirs, gitPushSwitch, schedulePushSwitch, intervalTime, firstTime);
	}
}
