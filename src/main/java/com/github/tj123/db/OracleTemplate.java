package com.github.tj123.db;

import com.github.tj123.bean.Po;

import java.util.*;

/**
 * Created by TJ on 2016/10/20.
 */
public class OracleTemplate extends DBTemplate {
	
	@Override
	Sql genUpdateSql(String table, Map<String, Object> data, Map<String, Object> where) throws Exception {
		Sql sql = new Sql();
		List<Object> list = new ArrayList<>();
		StringBuilder sb = new StringBuilder("update " + table + " set ");
		StringBuilder dataSql = new StringBuilder();
		Set<Map.Entry<String, Object>> dataEntries = data.entrySet();
		for (Map.Entry<String, Object> entry : dataEntries) {
			dataSql.append(entry.getKey() + " = ?,");
			list.add(entry.getValue());
		}
		if (dataEntries.size() > 0) {
			dataSql.deleteCharAt(dataSql.length() - 1);
		}
		Set<Map.Entry<String, Object>> whereEntries = where.entrySet();
		StringBuilder whereSql = new StringBuilder();
		for (Map.Entry<String, Object> entry : whereEntries) {
			whereSql.append(entry.getKey() + " = ?,");
			list.add(entry.getValue());
		}
		if (whereEntries.size() > 0) {
			whereSql.deleteCharAt(whereSql.length() - 1);
		}
		sb.append(dataSql).append(" where ").append(whereSql);
		sql.setSql(sb.toString());
		sql.setData(list.toArray());
		return sql;
	}
	
	@Override
	public void upsert(Po po, String... keys) throws Exception {
		
	}
	
	@Override
	public PageResult findPage(String sql, Page page, Object... params) throws Exception {
		return null;
	}
	
	@Override
	Sql genPageSql(String sql, Page page, Object[] params) throws Exception {
		Sql sq = new Sql();
		List<Object> list = new ArrayList<>(Arrays.asList(params));
		sq.setSql(new StringBuilder("select * from (select rSa.*,rownum rn from (").append(sql)
				.append(") rSa where rn > ? and rn <= ?").toString());
		list.add(page.getSize() * (page.getPage() - 1));
		list.add(page.getSize() * page.getPage());
		return sq;
	}
}
