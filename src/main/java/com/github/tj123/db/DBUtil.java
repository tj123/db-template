package com.github.tj123.db;

import com.github.tj123.bean.Po;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TJ on 2016/10/19.
 */
public class DBUtil {
	
	private static Log log = LogFactory.getLog(DBUtil.class);
	
	/**
	 * 还原SQL语句
	 *
	 * @param sql
	 * @param params
	 * @return
	 */
	public static String restoreSql(String sql, Object... params) {
		if (sql == null) sql = "";
		for (Object param : params) {
			sql = sql.replaceFirst("\\?", param instanceof String ? "'"
					+ String.valueOf(param) + "'" : String.valueOf(param));
		}
		return sql;
	}
	
	/**
	 * po 转为map @Column 起作用
	 *
	 * @param po
	 * @return
	 */
	public static Map<String, Object> poToMap(Po po) throws Exception {
		return poToMap(po, true);
	}
	
	/**
	 * po 转为map @Column 起作用
	 * withNull 是否要把 null 字段写入 map
	 *
	 * @param po
	 * @return
	 */
	public static Map<String, Object> poToMap(Po po, boolean withNull) throws Exception {
		Class<? extends Po> clazz = po.getClass();
		Map<String, Object> map = new HashMap<>();
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getName().equals("serialVersionUID")) {
				continue;
			}
			field.setAccessible(true);
			Column column = field.getAnnotation(Column.class);
			Object value = field.get(po);
			if (withNull && value == null) {
				continue;
			}
			Class<?> fieldClass = field.getType();
			if(fieldClass.isEnum()){
				try{
					String key = String.valueOf(fieldClass.getMethod("getKey").invoke(value));
					if (key != null && !key.trim().equals("") && !"null".equals(key)) {
						value = key;
					}
				}catch (Exception e){
				}
			}
			map.put(column != null ? column.value() : field.getName(), value);
		}
		return map;
	}
	
}
