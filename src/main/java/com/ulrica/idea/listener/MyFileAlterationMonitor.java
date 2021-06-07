package com.ulrica.idea.listener;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.ulrica.idea.configurable.PropertiesConfigurable;
import com.ulrica.idea.configurable.PropertiesDetail;
import com.ulrica.idea.persistent.SettingPersistent;
import com.ulrica.idea.utils.ProjectUtil;
import com.ulrica.idea.utils.ScheduleUtil;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

public class MyFileAlterationMonitor {

    private static final Logger LOG = Logger.getInstance(MyFileAlterationMonitor.class);

    private String path;        // 文件夹目录

    private String fileSuffix;    // 需要监听的文件名后缀

    private final long interval;        // 监听间隔

    private static final long DEFAULT_INTERVAL = 10 * 1000; // 默认监听间隔10s

    public static FileAlterationListenerAdaptor listener;    // 事件处理类对象

    public MyFileAlterationMonitor(String path, String fileSuffix, FileAlterationListenerAdaptor listenerAdaptor) {
        this.path = path;
        this.fileSuffix = fileSuffix;
        this.interval = DEFAULT_INTERVAL;
        listener = listenerAdaptor;
    }

    /***
     * 开启监听
     */
    public void start() {
        Project currentProject = ProjectUtil.getCurrentProject();
        SettingPersistent settingPersistent = SettingPersistent.getInstance(currentProject);
        if (settingPersistent.schedulePushSwitch) {
            ScheduleUtil.configSchedule(currentProject);
        } else {
            PropertiesDetail propertiesDetail = PropertiesConfigurable.readProperties(currentProject);
            if (propertiesDetail != null && propertiesDetail.isSchedulePushSwitch()) {
                LOG.info("开始从配置文件中读取定时导出yapi任务配置");
                //配置定时推送任务
                ScheduleUtil.configScheduleByProperties(currentProject, propertiesDetail);
            }
        }
        if (path == null) {
            throw new IllegalStateException("Listen path must not be null");
        }
        if (listener == null) {
            throw new IllegalStateException("Listener must not be null");
        }
        FileAlterationObserver observer = new FileAlterationObserver(path,
                FileFilterUtils.suffixFileFilter(fileSuffix));
        observer.addListener(listener);
        FileAlterationMonitor monitor = new FileAlterationMonitor(interval);
        monitor.addObserver(observer);
        try {
            monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

