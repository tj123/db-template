package com.github.tj123.db.operate;

import com.github.tj123.bean.Po;
import com.github.tj123.db.PrimaryKey;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 本包用的工具类
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

	/**
	 * 获取主键 field
	 *
	 * @param clazz
	 * @return
	 */
	public static Field getPrimaryKeyField(Class<? extends Po> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.getAnnotation(PrimaryKey.class) != null) {
				return field;
			}
		}
		return null;
	}
	
}
