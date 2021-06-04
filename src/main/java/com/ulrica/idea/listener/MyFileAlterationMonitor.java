package com.ulrica.idea.listener;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

public class MyFileAlterationMonitor {

    private String path;        // 文件夹目录

    private String fileSuffix;    // 需要监听的文件名后缀

    private final long interval;        // 监听间隔

    private static final long DEFAULT_INTERVAL = 10 * 1000; // 默认监听间隔10s

    private FileAlterationListenerAdaptor listener;    // 事件处理类对象

    public MyFileAlterationMonitor(String path, String fileSuffix, FileAlterationListenerAdaptor listenerAdaptor) {
        this.path = path;
        this.fileSuffix = fileSuffix;
        this.interval = DEFAULT_INTERVAL;
        this.listener = listenerAdaptor;
    }

    /***
     * 开启监听
     */
    public void start() {
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

