package com.ulrica.idea.gui;

import com.intellij.openapi.project.Project;
import com.ulrica.idea.utils.ProjectUtil;
import com.ulrica.idea.verifier.DirInputVerifier;
import com.ulrica.idea.verifier.NumbericInputVerifier;
import com.ulrica.idea.verifier.TimestampInputVerifier;

import javax.swing.*;

/**
 * @author 80275131
 * @version 1.0
 * @date 2021/2/18 19:31
 * @since 1.0
 **/
public class SettingGUI {
	private JPanel rootPanel;
	private JTextField listenDirs;
	private JTextArea exportDirs;
	private JCheckBox gitPushSwitch;
	private JCheckBox schedulePushSwitch;
	private JTextField firstTime;
	private JTextField intervalTime;


	public void onCreate() {
		DirInputVerifier dirInputVerifier = new DirInputVerifier();
		this.listenDirs.setInputVerifier(dirInputVerifier);
		this.exportDirs.setInputVerifier(dirInputVerifier);

		TimestampInputVerifier timestampInputVerifier = new TimestampInputVerifier();
		this.firstTime.setInputVerifier(timestampInputVerifier);

		this.intervalTime.setInputVerifier(new NumbericInputVerifier());
	}

	public JPanel getRootPanel() {
		return rootPanel;
	}

    public String getListenDirs() {
        Project currentProject = ProjectUtil.getCurrentProject();
        String basePath = currentProject.getBasePath();
        return basePath + "/" + listenDirs;
    }

	public void setListenDirs(String listenDirs) {
		this.listenDirs.setText(listenDirs);
	}

    public String getExportDirs() {
        Project currentProject = ProjectUtil.getCurrentProject();
        String basePath = currentProject.getBasePath();
        return basePath + "/" + exportDirs;
    }

	public void setExportDirs(String exportDirs) {
		this.exportDirs.setText(exportDirs);
	}

	public boolean getGitPushSwitch() {
		return gitPushSwitch.isSelected();
	}

	public void setGitPushSwitch(boolean gitPushSwitch) {
		this.gitPushSwitch.setSelected(gitPushSwitch);
	}

	public boolean getSchedulePushSwitch() {
		return schedulePushSwitch.isSelected();
	}

	public void setSchedulePushSwitch(boolean schedulePushSwitch) {
		this.schedulePushSwitch.setSelected(schedulePushSwitch);
	}

	public String getFirstTime() {
		return firstTime.getText();
	}

	public void setFirstTime(String cron) {
		this.firstTime.setText(cron);
	}

	public String getIntervalTime() {
		return intervalTime.getText();
	}

	public void setIntervalTime(String intervalTime) {
		this.intervalTime.setText(intervalTime);
	}

}
