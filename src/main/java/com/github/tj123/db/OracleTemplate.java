package com.github.tj123.db;

import com.github.tj123.db.operate.QueryInfo;
import com.github.tj123.db.operate.Sql;
import com.github.tj123.db.operate.UpdateSql;
import com.github.tj123.db.operate.WhereEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		Sql oracle = whereEquals.getOracle();
		return queryForObject(new StringBuilder("select count(1) from ").append(table).append(" where ")
				.append(oracle.getContent()).toString(), oracle.getParams().toArray(), Integer.class);
		
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
