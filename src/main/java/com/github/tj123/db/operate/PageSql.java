package com.github.tj123.db.operate;

/**
 * 分页查询封装
 */
public class PageSql implements DBSql {
	
	public PageSql(QueryInfo queryInfo) {
		this.queryInfo = queryInfo;
	}

	private QueryInfo queryInfo;

	@Override
	public Sql getMysql() throws Exception {
		return null;
	}
	
	@Override
	public Sql getOracle() throws Exception {
		return null;
	}
}
