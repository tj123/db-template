package com.github.tj123.db;

import com.github.tj123.bean.Po;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by TJ on 2016/10/20.
 */
public class MysqlTemplate extends DBTemplate {
	
	private Log log = LogFactory.getLog(DBTemplate.class);
	
	public String save(Po po) throws Exception {
		Class<? extends Po> poClass = po.getClass();
		Table table = poClass.getAnnotation(Table.class);
		if (table == null) {
			log.debug("@Table 没有找到！", new Exception("@Table 没有找到！"));
			return "";
		}
		Field field = getPrimaryKeyField(poClass);
		if (field == null) {
			log.debug("@PrimaryKey 没有找到！", new Exception("@PrimaryKey 没有找到！"));
			return "";
		}
		field.setAccessible(true);
		String uuid = String.valueOf(field.get(po));
		if (uuid == null || uuid.trim().equals("") || "null".equals(uuid)) {
			field.set(po, uuid = po.getUUID()); //通过java生成主键方式生成主键
		}
		new SimpleJdbcInsert(getDataSource()).withTableName(table.value()).execute(DBUtil.poToMap(po, false));
		return uuid;
	}
	
	/**
	 * 只对同一张表批量插入
	 * @param pos
	 */
	public <PO extends Po> void save(List<PO> pos) throws Exception {
		if(pos == null || pos.size() == 0) return;
		Class<? extends Po> poClass = pos.get(0).getClass();
		Table table = poClass.getAnnotation(Table.class);
		if (table == null) {
			log.debug("@Table 没有找到！", new Exception("@Table 没有找到！"));
			return;
		}
		Field field = getPrimaryKeyField(poClass);
		if (field == null) {
			log.debug("@PrimaryKey 没有找到！", new Exception("@PrimaryKey 没有找到！"));
			return;
		}
		field.setAccessible(true);
		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(getDataSource()).withTableName(table.value());
		List<Map<String,Object>> list = new ArrayList<>();
		for (Po po : pos) {
			String primaryKey = String.valueOf(field.get(po));
			if (primaryKey == null || primaryKey.trim().equals("") || "null".equals(primaryKey)) {
				field.set(po,po.getUUID());
			}
			list.add(DBUtil.poToMap(po));
		}
		simpleJdbcInsert.executeBatch(list.toArray(new HashMap[]{}));
	}
	
	@Override
	Sql genUpdateSql(String table, Map<String, Object> data, Map<String, Object> where) throws Exception {
		Sql sql = new Sql();
		List<Object> list = new ArrayList<>();
		StringBuilder sb = new StringBuilder("update `" + table + "` set ");
		StringBuilder dataSql = new StringBuilder();
		Set<Map.Entry<String, Object>> dataEntries = data.entrySet();
		for (Map.Entry<String, Object> entry : dataEntries) {
			dataSql.append("`" + entry.getKey() + "` = ?,");
			list.add(entry.getValue());
		}
		if (dataEntries.size() > 0) {
			dataSql.deleteCharAt(dataSql.length() - 1);
		}
		Set<Map.Entry<String, Object>> whereEntries = where.entrySet();
		StringBuilder whereSql = new StringBuilder();
		for (Map.Entry<String, Object> entry : whereEntries) {
			whereSql.append("`" + entry.getKey() + "` = ?,");
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
	Sql genPageSql(String sql, Page page, Object[] params) throws Exception {
		Sql sq = new Sql();
		sq.setSql(new StringBuffer(sql).append(" limit ?,?").toString());
		List<Object> list = new ArrayList<>(Arrays.asList(params));
		list.add((page.getPage() - 1) * page.getSize());
		list.add(page.getSize());
		sq.setData(list.toArray());
		return sq;
	}
	
}
