package com.oppo.ads.utils;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * 时间戳处理工具
 *
 * @author 80275131
 * @version 1.0
 * @date 2021/2/19 17:15
 * @since 1.0
 **/
public class TimestampUtil {

	public static boolean isTimestamp(String cs) {
		return NumberUtils.isDigits(cs) && cs.length() == 13;
	}

	/**
	 * 根据指定开始时间以及间隔时间，计算出最近可执行的延时
	 *
	 * @return
	 */
	public static long calcRecentlyDelayTime(long firstTime, long intervalTime) {
		long currentTimeMillis = System.currentTimeMillis();
		while (currentTimeMillis >= firstTime) {
			firstTime += intervalTime;
		}
		return firstTime - currentTimeMillis;
	}

}
