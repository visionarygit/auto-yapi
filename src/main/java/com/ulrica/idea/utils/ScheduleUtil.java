package com.ulrica.idea.utils;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.ulrica.idea.configurable.PropertiesDetail;
import com.ulrica.idea.extensions.YapiExporter;
import com.ulrica.idea.persistent.SettingPersistent;
import com.ulrica.idea.service.ScheduleService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.concurrent.TimeUnit;

/**
 * 定时任务工具
 *
 * @author 80275131
 * @version 1.0
 * @date 2021/2/19 17:06
 * @since 1.0
 **/
public class ScheduleUtil {
	private static final Logger LOG = Logger.getInstance(ScheduleUtil.class);

	public static void configSchedule(Project project) {
		ScheduleService scheduleService = ScheduleService.getInstance();
		SettingPersistent settingPersistent = SettingPersistent.getInstance(project);
		//清除原有所有任务
		scheduleService.clearSchedule();

		if (settingPersistent == null) {
			return;
		}
		if (!settingPersistent.schedulePushSwitch) {
			LOG.info("schedulePushSwitch not open,schedule cancel...");
			return;
		}
		if (StringUtils.isBlank(settingPersistent.exportDirs)) {
			LOG.info("exportDirs not config,schedule cancel...");
			return;
		}
		if (!TimestampUtil.isTimestamp(settingPersistent.firstTime) || !NumberUtils.isDigits(settingPersistent.intervalTime)) {
			LOG.info("firstTime or intervalTime is invalid,schedule cancel...");
			return;
		}
		long firstTime = Long.parseLong(settingPersistent.firstTime);
		long intervalTime = Long.parseLong(settingPersistent.intervalTime);
		long delayTime = TimestampUtil.calcRecentlyDelayTime(firstTime, intervalTime);
		scheduleService.scheduleAtFixedRate(() -> {
			String[] split = settingPersistent.exportDirsAbsolute.trim().split(",");
			for (String s : split) {
				YapiExporter.export(project, s);
			}
		}, delayTime, intervalTime, TimeUnit.MILLISECONDS);
		LOG.info("schedule config.....delayTime: " + delayTime + ",intervalTime:" + intervalTime);
		NotificationUtil.notifyInfo("AutoYapi", "定时导出将在" + delayTime + "ms 后开始执行," + intervalTime + "ms 执行一次");
	}

    public static void configScheduleByProperties(Project project, PropertiesDetail propertiesDetail) {
        ScheduleService scheduleService = ScheduleService.getInstance();
        scheduleService.clearSchedule();
        if (propertiesDetail == null) {
            return;
        }
        if (!propertiesDetail.isSchedulePushSwitch()) {
            LOG.info("schedulePushSwitch not open,schedule cancel...");
            return;
        }
        if (StringUtils.isBlank(propertiesDetail.getExportDirs())) {
            LOG.info("exportDirs not config,schedule cancel...");
            return;
        }
        if (!TimestampUtil.isTimestamp(propertiesDetail.getFirstTime()) || !NumberUtils.isDigits(propertiesDetail.getIntervalTime())) {
            LOG.info("firstTime or intervalTime is invalid,schedule cancel...");
            return;
        }
        long firstTime = Long.parseLong(propertiesDetail.getFirstTime());
        long intervalTime = Long.parseLong(propertiesDetail.getIntervalTime());
        long delayTime = TimestampUtil.calcRecentlyDelayTime(firstTime, intervalTime);
        scheduleService.scheduleAtFixedRate(() -> {
            String[] split = propertiesDetail.getExportDirs().trim().split(",");
            for (String s : split) {
                YapiExporter.export(project, s);
            }
        }, delayTime, intervalTime, TimeUnit.MILLISECONDS);
        LOG.info("schedule config.....delayTime: " + delayTime + ",intervalTime:" + intervalTime);
        NotificationUtil.notifyInfo("AutoYapi", "定时导出将在" + delayTime + "ms 后开始执行," + intervalTime + "ms 执行一次");
    }
}
