package com.github.tj123.db;

import com.github.tj123.db.operate.QueryInfo;
import com.github.tj123.db.operate.Sql;
import com.github.tj123.db.operate.UpdateSql;
import com.github.tj123.db.operate.WhereEquals;

import java.util.*;

/**
 * Oracle操作对象
 */
public class OracleTemplate extends DBTemplate {
	
	@Override
	protected Sql genUpdateSql(QueryInfo queryInfo) throws Exception {
		return new UpdateSql(queryInfo).getOracle();
	}
	
	@Override
	protected int findCount(String table, WhereEquals whereEquals) throws Exception {
		return 0;
	}
	
	@Override
	public PageResult findPage(String sql, Page page, Object... params) throws Exception {
		return null;
	}
	
	@Override
	Sql genPageSql(String sql, Page page, Object[] params) throws Exception {
		Sql sq = new Sql();
		List<Object> list = new ArrayList<>(Arrays.asList(params));
		sq.setContent(new StringBuilder("select * from (select rSa.*,rownum rn from (").append(sql)
				.append(") rSa where rn > ? and rn <= ?"));
		list.add(page.getSize() * (page.getPage() - 1));
		list.add(page.getSize() * page.getPage());
		return sq;
	}
}
