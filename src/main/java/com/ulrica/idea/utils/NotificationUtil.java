package com.ulrica.idea.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

/**
 * 通知工具类
 *
 * @author 80275131
 * @version 1.0
 * @date 2021/2/19 17:39
 * @since 1.0
 **/
public class NotificationUtil {

	public static void notifyInfo(String titile, String content) {
		Notification notification = new Notification("other", titile, content, NotificationType.INFORMATION);
		Notifications.Bus.notify(notification);
	}
}
