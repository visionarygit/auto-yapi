package com.oppo.ads.configurable;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesConfigurable {

    public static PropertiesDetail readProperties(Class cla){
        PropertiesDetail propertiesDetail = new PropertiesDetail();
        Properties properties = new Properties();
        try (InputStream in = cla.getClassLoader().getResourceAsStream("setting.properties")) {
            properties.load(in);
            propertiesDetail.setExportDirs(properties.getProperty("exportDirs"));
            propertiesDetail.setListenDirs(properties.getProperty("listenDirs"));
            propertiesDetail.setSchedulePushSwitch((Boolean) properties.get("schedulePushSwitch"));
            propertiesDetail.setGitPushSwitch((Boolean) properties.get("gitPushSwitch"));
            propertiesDetail.setFirstTime(properties.getProperty("firstTime"));
            propertiesDetail.setIntervalTime(properties.getProperty("intervalTime"));
            return propertiesDetail;
        }catch (Exception e){
            System.out.println("文件未找到！从gui配置中读取");
            return null;
        }
    }
}
