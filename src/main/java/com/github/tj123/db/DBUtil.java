package com.github.tj123.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;

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
	
	
}
