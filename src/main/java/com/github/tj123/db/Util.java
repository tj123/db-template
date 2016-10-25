package com.github.tj123.db;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by TJ on 2016/10/25.
 */
class Util {
	
	/**
	 *  日期转换
	 */
	public static String dateToString(Date date, String pattern) throws Exception{
		return new SimpleDateFormat(pattern).format(date);
	}
	
	/**
	 * 判断是否为父类
	 *
	 * @param supperClass
	 * @param subClass
	 * @return
	 */
	public static boolean isSuperClass(Class<?> supperClass, Class<?> subClass) {
		return supperClass.isAssignableFrom(subClass);
	}
	
}
