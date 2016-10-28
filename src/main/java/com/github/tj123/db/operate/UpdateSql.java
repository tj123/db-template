package com.github.tj123.db.operate;

import java.util.List;
import java.util.Map;

/**
 * 目前只支持等于
 */
public class UpdateSql implements DBSql{
	
	private QueryInfo queryInfo;
	
	/**
	 *  目前只支持等于
	 */
	public UpdateSql(QueryInfo queryInfo){
		this.queryInfo = queryInfo;
	}
	
	@Override
	public Sql getMysql() throws Exception {
		Sql sql = new Sql();
		WhereEquals whereEquals = queryInfo.getWhereEquals();
		sql.setSql(new StringBuilder("update `").append(queryInfo.getTable()).append("` set "));
		Sql set = new Sets(queryInfo.getData()).getMysql();
		sql.getSql().append(set.getSql()).append(" where ");
		sql.setParams(set.getParams()).append(whereEquals.getMysql());
		return sql;
	}
	
	@Override
	public Sql getOracle() throws Exception {
		Sql sql = new Sql();
		WhereEquals whereEquals = queryInfo.getWhereEquals();
		sql.setSql(new StringBuilder("update ").append(queryInfo.getTable()).append(" set "));
		Sql set = new Sets(queryInfo.getData()).getOracle();
		sql.getSql().append(set.getSql()).append(" where ");
		sql.setParams(set.getParams()).append(whereEquals.getOracle());
		return sql;
	}
}
