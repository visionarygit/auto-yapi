package com.ulrica.idea.configurable;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.project.Project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertiesConfigurable {

    public static PropertiesDetail readProperties(Project currentProject){
        BufferedReader bufferedReader = null;
        try {
            String basePath = currentProject.getBasePath();
            basePath += "/.yapi.config";
            Log.info("getBasePath: " + basePath);
            Properties properties = new Properties();
            // 使用InPutStream流读取properties文件
            bufferedReader = new BufferedReader(new FileReader(basePath));
            properties.load(bufferedReader);
            PropertiesDetail propertiesDetail = new PropertiesDetail();
            try {
                propertiesDetail.setExportDirs(properties.getProperty("exportDirs"));
                propertiesDetail.setListenDirs(properties.getProperty("listenDirs"));
                propertiesDetail.setSchedulePushSwitch(convertStringToBoolean(properties.getProperty("schedulePushSwitch")));
                propertiesDetail.setGitPushSwitch(convertStringToBoolean(properties.getProperty("gitPushSwitch")));
                propertiesDetail.setFirstTime(properties.getProperty("firstTime"));
                propertiesDetail.setIntervalTime(properties.getProperty("intervalTime"));
            }catch (Exception e){
                e.printStackTrace();
                Log.info("配置文件读取错误");
            }
            return propertiesDetail;
        } catch (Exception e) {
            e.printStackTrace();
            Log.info("文件未找到!");
            return null;
        }finally {
            if(bufferedReader != null){
                try{
                    bufferedReader.close();
                }catch (IOException e){
                    Log.info("bufferedReader close IOException:" + e.getMessage());
                }
            }

        }
    }

    private static Boolean convertStringToBoolean(String str) throws Exception {
        Log.info(str);
        if(str.equals("true")){
            return true;
        }
        if(str.equals("false")){
            return false;
        }
        throw new Exception("请正确输入布尔值，true或者false");
    }
}
