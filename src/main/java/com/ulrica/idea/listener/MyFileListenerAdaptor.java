package com.ulrica.idea.listener;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.ulrica.idea.configurable.PropertiesConfigurable;
import com.ulrica.idea.configurable.PropertiesDetail;
import com.ulrica.idea.service.ScheduleService;
import com.ulrica.idea.utils.ProjectUtil;
import com.ulrica.idea.utils.ScheduleUtil;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

import java.io.File;

public class MyFileListenerAdaptor extends FileAlterationListenerAdaptor {
    private static final Logger LOG = Logger.getInstance(MyFileListenerAdaptor.class);

    @Override
    public void onFileChange(File file) {
        LOG.info("监听到yapi配置文件变化！");
        if(ScheduleService.ifIdeaHasConfig){
            return;
        }
        super.onFileChange(file);
        PropertiesDetail propertiesDetail = PropertiesConfigurable.readPropertiesByAbsolutePath(file.getAbsolutePath());
        Project currentProject = ProjectUtil.getCurrentProject();
        if (propertiesDetail != null && propertiesDetail.isSchedulePushSwitch()) {
            LOG.info("开始从配置文件中读取定时导出yapi任务配置");
            //配置定时推送任务
            ScheduleUtil.configScheduleByProperties(currentProject, propertiesDetail);
        }
    }
}