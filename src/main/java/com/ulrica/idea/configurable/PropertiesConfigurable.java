package com.ulrica.idea.configurable;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.project.Project;
import com.ulrica.idea.listener.MyFileAlterationMonitor;
import com.ulrica.idea.listener.MyFileListenerAdaptor;

import java.io.*;
import java.util.Properties;

public class PropertiesConfigurable {

    public static PropertiesDetail readProperties(Project currentProject) {
        BufferedReader bufferedReader = null;
        try {
            String basePath = currentProject.getBasePath();
            basePath += "/.yapi.config";
            Log.info("getBasePath: " + basePath);
            // 使用InPutStream流读取properties文件
            bufferedReader = new BufferedReader(new FileReader(basePath));
            return getPropertiesDetail(bufferedReader);
        } catch (Exception e) {
            e.printStackTrace();
            Log.info("配置文件读取错误");
            return null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.info("bufferedReader close IOException:" + e.getMessage());
                }
            }

        }
    }

    public static void changeFile(Project project) {
        MyFileAlterationMonitor.listener.onFileChange(new File(project.getBasePath() + "/.yapi.config"));
    }

    public static PropertiesDetail readPropertiesByAbsolutePath(String basePath) {
        BufferedReader bufferedReader = null;
        try {
            Log.info("getBasePath: " + basePath);
            // 使用InPutStream流读取properties文件
            bufferedReader = new BufferedReader(new FileReader(basePath));
            return getPropertiesDetail(bufferedReader);
        } catch (Exception e) {
            e.printStackTrace();
            Log.info("配置文件读取错误");
            return null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.info("bufferedReader close IOException:" + e.getMessage());
                }
            }

        }
    }

    private static PropertiesDetail getPropertiesDetail(BufferedReader bufferedReader) throws Exception {
        Properties properties = new Properties();
        // 使用InPutStream流读取properties文件
        properties.load(bufferedReader);
        PropertiesDetail propertiesDetail = new PropertiesDetail();
        propertiesDetail.setExportDirs(properties.getProperty("exportDirs"));
        propertiesDetail.setListenDirs(properties.getProperty("listenDirs"));
        propertiesDetail.setSchedulePushSwitch(convertStringToBoolean(properties.getProperty("schedulePushSwitch")));
        propertiesDetail.setGitPushSwitch(convertStringToBoolean(properties.getProperty("gitPushSwitch")));
        propertiesDetail.setFirstTime(properties.getProperty("firstTime"));
        propertiesDetail.setIntervalTime(properties.getProperty("intervalTime"));
        return propertiesDetail;
    }

    private static Boolean convertStringToBoolean(String str) throws Exception {
        Log.info(str);
        if (str.equals("true")) {
            return true;
        }
        if (str.equals("false")) {
            return false;
        }
        throw new Exception("请正确输入布尔值，true或者false");
    }
}
