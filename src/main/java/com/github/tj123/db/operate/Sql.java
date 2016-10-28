package com.github.tj123.db.operate;

import java.util.List;

/**
 * 用于查询的参数
 */
public class Sql {
	
	private StringBuilder sql;
	
	private List<Object> params;
	
	public StringBuilder getSql() {
		return sql;
	}
	
	public Sql setSql(StringBuilder sql) {
		this.sql = sql;
		return this;
	}
	
	public Sql append(Sql sql){
		this.sql.append(sql.getSql());
		this.params.addAll(sql.getParams());
		return this;
	}
	
	public List<Object> getParams() {
		return params;
	}
	
	public Sql setParams(List<Object> params) {
		this.params = params;
		return this;
	}
}
