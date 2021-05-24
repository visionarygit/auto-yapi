package com.oppo.ads.configurable;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.oppo.ads.gui.SettingGUI;
import com.oppo.ads.persistent.SettingPersistent;
import com.oppo.ads.utils.ProjectUtil;
import com.oppo.ads.utils.ScheduleUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 自动导出配置
 *
 * @author 80275131
 * @version 1.0
 * @date 2021/2/18 19:39
 * @since 1.0
 **/
public class SettingConfigurable implements SearchableConfigurable {

	private SettingGUI settingGUI;

	@NotNull
	@Override
	public String getId() {
		return "AutoYapi.SettingConfigurable";
	}

	@Override
	public @NlsContexts.ConfigurableName String getDisplayName() {
		return "AutoYapi";
	}

	@Nullable
	@Override
	public JComponent createComponent() {
		settingGUI = new SettingGUI();
		settingGUI.onCreate();
		return settingGUI.getRootPanel();
	}

	@Override
	public boolean isModified() {
		SettingPersistent settingPersistent = SettingPersistent.getInstance(ProjectUtil.getCurrentProject());
		boolean modified = !settingPersistent.firstTime.equals(settingGUI.getFirstTime());
		modified |= !settingPersistent.intervalTime.equals(settingGUI.getIntervalTime());
		modified |= settingPersistent.gitPushSwitch != settingGUI.getGitPushSwitch();
		modified |= settingPersistent.schedulePushSwitch != settingGUI.getSchedulePushSwitch();
		modified |= !settingPersistent.exportDirs.equals(settingGUI.getExportDirs());
		modified |= !settingPersistent.listenDirs.equals(settingGUI.getListenDirs());
		return modified;
	}

//	@Override
//	public void apply() throws ConfigurationException {
//		Project currentProject = ProjectUtil.getCurrentProject();
//		SettingPersistent settingPersistent = SettingPersistent.getInstance(currentProject);
//		settingPersistent.listenDirs = settingGUI.getListenDirs();
//		settingPersistent.exportDirs = settingGUI.getExportDirs();
//		settingPersistent.schedulePushSwitch = settingGUI.getSchedulePushSwitch();
//		settingPersistent.gitPushSwitch = settingGUI.getGitPushSwitch();
//		settingPersistent.firstTime = settingGUI.getFirstTime();
//		settingPersistent.intervalTime = settingGUI.getIntervalTime();
//		//配置定时推送任务
//		ScheduleUtil.configSchedule(currentProject);
//	}

    @Override
    public void apply() throws ConfigurationException {
        Project currentProject = ProjectUtil.getCurrentProject();
        SettingPersistent settingPersistent = SettingPersistent.getInstance(currentProject);
        try {
            PropertiesDetail properties = PropertiesConfigurable.readProperties(this.getClass());
            assert properties != null;
            settingPersistent.listenDirs = properties.getListenDirs();
            settingPersistent.exportDirs = properties.getExportDirs();
            settingPersistent.schedulePushSwitch = properties.isSchedulePushSwitch();
            settingPersistent.gitPushSwitch = properties.isGitPushSwitch();
            settingPersistent.firstTime = properties.getFirstTime();
            settingPersistent.intervalTime = properties.getIntervalTime();
        }catch (Exception e){
            Log.info("配置文件读取错误，从gui中读取");
            settingPersistent.listenDirs = settingGUI.getListenDirs();
            settingPersistent.exportDirs = settingGUI.getExportDirs();
            settingPersistent.schedulePushSwitch = settingGUI.getSchedulePushSwitch();
            settingPersistent.gitPushSwitch = settingGUI.getGitPushSwitch();
            settingPersistent.firstTime = settingGUI.getFirstTime();
            settingPersistent.intervalTime = settingGUI.getIntervalTime();
        }
        //配置定时推送任务
        ScheduleUtil.configSchedule(currentProject);
    }

	@Override
	public void reset() {
		SettingPersistent settingPersistent = SettingPersistent.getInstance(ProjectUtil.getCurrentProject());
		settingGUI.setFirstTime(settingPersistent.firstTime);
		settingGUI.setIntervalTime(settingPersistent.intervalTime);
		settingGUI.setExportDirs(settingPersistent.exportDirs);
		settingGUI.setGitPushSwitch(settingPersistent.gitPushSwitch);
		settingGUI.setSchedulePushSwitch(settingPersistent.schedulePushSwitch);
		settingGUI.setListenDirs(settingPersistent.listenDirs);
	}


}
