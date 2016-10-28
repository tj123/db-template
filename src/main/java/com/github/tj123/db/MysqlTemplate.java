package com.github.tj123.db;

import com.github.tj123.bean.Po;
import com.github.tj123.db.operate.QueryInfo;
import com.github.tj123.db.operate.Sql;
import com.github.tj123.db.operate.UpdateSql;
import com.github.tj123.db.operate.WhereEquals;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Mysql 操作对象
 */
public class MysqlTemplate extends DBTemplate {
	
	private Log log = LogFactory.getLog(DBTemplate.class);
	
	public String save(Po po) throws Exception {
		QueryInfo queryInfo = new QueryInfo(po);
		Field field = queryInfo.getPrimaryField();
		field.setAccessible(true);
		String uuid = String.valueOf(field.get(po));
		if (uuid == null || uuid.trim().equals("") || "null".equals(uuid)) {
			field.set(po, uuid = po.getUUID()); //通过java生成主键方式生成主键
		}
		new SimpleJdbcInsert(getDataSource()).withTableName(queryInfo.getTable()).execute(DBUtil.poToMap(po, false));
		return uuid;
	}
	
	/**
	 * 只对同一张表批量插入
	 *
	 * @param pos
	 */
	public <PO extends Po> void save(List<PO> pos) throws Exception {
		if (pos == null || pos.size() == 0) return;
		QueryInfo queryInfo = new QueryInfo(pos.get(0));
		Field field = queryInfo.getPrimaryField();
		field.setAccessible(true);
		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(getDataSource()).withTableName(queryInfo.getTable());
		List<Map<String, Object>> list = new ArrayList<>();
		for (Po po : pos) {
			String primaryKey = String.valueOf(field.get(po));
			if (primaryKey == null || primaryKey.trim().equals("") || "null".equals(primaryKey)) {
				field.set(po, po.getUUID());
			}
			list.add(DBUtil.poToMap(po));
		}
		simpleJdbcInsert.executeBatch(list.toArray(new HashMap[]{}));
	}
	
	@Override
	protected Sql genUpdateSql(QueryInfo queryInfo) throws Exception {
		return new UpdateSql(queryInfo).getMysql();
	}
	
	@Override
	protected int findCount(String table, WhereEquals whereEquals) throws Exception {
		return 0;
	}
	
	
	@Override
	Sql genPageSql(String sql, Page page, Object[] params) throws Exception {
		Sql sq = new Sql();
		sq.setSql(new StringBuilder(sql).append(" limit ?,?"));
		List<Object> list = new ArrayList<>(Arrays.asList(params));
		list.add((page.getPage() - 1) * page.getSize());
		list.add(page.getSize());
		sq.setParams(list);
		return sq;
	}
	
}
