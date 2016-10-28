package com.github.tj123.db.operate;

import java.util.*;

/**
 * update 语句中的 set
 */
public class Sets implements DBSql {
	
	private Map<String, Object> sets;
	private Sql sql;
	private StringBuilder sb;
	private List<Object> list;
	private Set<Map.Entry<String, Object>> entries;
	
	public Sets(Map<String, Object> sets) {
		this.sets = sets;
		sql = new Sql();
		sb = new StringBuilder();
		list = new ArrayList<>();
		entries = sets.entrySet();
	}
	
	@Override
	public Sql getMysql() throws Exception {
		for (Map.Entry<String, Object> entry : entries) {
			sb.append("`").append(entry.getKey());
			Object value = entry.getValue();
			if (value instanceof Date) {
				sb.append("` = date_format(?,'%Y-%m-%d %H:%i:%s'),");
				list.add(Util.dateToString((Date) value, "yyyy-MM-dd HH:mm:ss"));
			} else {
				sb.append("` = ?,");
				list.add(value);
			}
		}
		if (entries.size() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sql.setSql(sb).setParams(list);
	}
	
	@Override
	public Sql getOracle() throws Exception {
		for (Map.Entry<String, Object> entry : entries) {
			sb.append(entry.getKey());
			Object value = entry.getValue();
			if (value instanceof Date) {
				sb.append(" = to_date(?,'yyyy-mm-dd hh24:mi:ss'),");
				list.add(Util.dateToString((Date) value, "yyyy-MM-dd HH:mm:ss"));
			} else {
				sb.append(" = ?,");
				list.add(value);
			}
		}
		if (entries.size() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sql.setSql(sb).setParams(list);
	}
}
