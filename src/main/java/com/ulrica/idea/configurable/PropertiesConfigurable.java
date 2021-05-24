package com.ulrica.idea.configurable;

import com.esotericsoftware.minlog.Log;

import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

public class PropertiesConfigurable {

    public static PropertiesDetail readProperties() {
        try {
            ResourceBundle properties = ResourceBundle.getBundle("setting");
            PropertiesDetail propertiesDetail = new PropertiesDetail();
            propertiesDetail.setExportDirs(properties.getString("exportDirs"));
            propertiesDetail.setListenDirs(properties.getString("listenDirs"));
            propertiesDetail.setSchedulePushSwitch((Boolean) properties.getObject("schedulePushSwitch"));
            propertiesDetail.setGitPushSwitch((Boolean) properties.getObject("gitPushSwitch"));
            propertiesDetail.setFirstTime(properties.getString("firstTime"));
            propertiesDetail.setIntervalTime(properties.getString("intervalTime"));
            return propertiesDetail;
        }catch (Exception e){
            Log.info("文件未找到！从gui配置中读取");
            return null;
        }
    }
}
