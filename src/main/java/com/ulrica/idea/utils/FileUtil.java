package com.ulrica.idea.utils;

import java.io.File;

/**
 * 文件工具类
 *
 * @author 80275131
 * @version 1.0
 * @date 2021/2/19 11:29
 * @since 1.0
 **/
public class FileUtil {

	public static boolean dirExists(String dir) {
		File file = new File(dir);
		return file.exists();
	}

}
