package com.ulrica.idea.configurable;


import com.intellij.openapi.project.Project;
import com.ulrica.idea.utils.ProjectUtil;

public class PropertiesDetail {
    private String listenDirs;
    private String exportDirs;
    private boolean gitPushSwitch;
    private boolean schedulePushSwitch;
    private String firstTime;
    private String intervalTime;

    public String getListenDirs() {
        Project currentProject = ProjectUtil.getCurrentProject();
        String basePath = currentProject.getBasePath();
        return basePath + "/" + listenDirs;
    }

    public void setListenDirs(String listenDirs) {
        this.listenDirs = listenDirs;
    }

    public String getExportDirs() {
        Project currentProject = ProjectUtil.getCurrentProject();
        String basePath = currentProject.getBasePath();
        return basePath + "/" + exportDirs;
    }

    public void setExportDirs(String exportDirs) {
        this.exportDirs = exportDirs;
    }

    public boolean isGitPushSwitch() {
        return gitPushSwitch;
    }

    public void setGitPushSwitch(boolean gitPushSwitch) {
        this.gitPushSwitch = gitPushSwitch;
    }

    public boolean isSchedulePushSwitch() {
        return schedulePushSwitch;
    }

    public void setSchedulePushSwitch(Boolean schedulePushSwitch) {
        this.schedulePushSwitch = schedulePushSwitch;
    }

    public String getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(String firstTime) {
        this.firstTime = firstTime;
    }

    public String getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(String intervalTime) {
        this.intervalTime = intervalTime;
    }
}
