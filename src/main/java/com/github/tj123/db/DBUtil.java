package com.github.tj123.db;

import com.github.tj123.bean.DateConvert;
import com.github.tj123.bean.Po;
import com.github.tj123.db.exception.DBException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.util.Date;
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
			DefaultCurrent defaultCurrent = field.getAnnotation(DefaultCurrent.class);
			Class<?> fieldClass = field.getType();
			Object value = field.get(po);
			if(defaultCurrent != null && value == null){
				if (Date.class.equals(fieldClass)) {
					map.put(column != null ? column.value() : field.getName(), new Date());
					continue;
				}else if(Util.isSuperClass(Date.class,fieldClass)){
					try {
						DateConvert dateConvert = (DateConvert) fieldClass.newInstance();
						dateConvert.setDate(new Date());
						map.put(column != null ? column.value() : field.getName(), dateConvert);
						continue;
					}catch (Exception e){
						throw new DBException("必须实现 DateConvert 接口",e);
					}
				}else {
					if (log.isErrorEnabled()) {
						log.error("不能识别的字段：" + field);
					}
				}
			}
			if (withNull && value == null) {
				continue;
			}
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
