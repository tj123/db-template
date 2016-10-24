package com.github.tj123.db;

/**
 * Created by TJ on 2016/10/24.
 */
class Sql {
	
	/**
	 * sql 语句
	 */
	private String sql;
	
	/**
	 *  值
	 */
	private Object[] data;
	
	public String getSql() {
		return sql;
	}
	
	public void setSql(String sql) {
		this.sql = sql;
	}
	
	public Object[] getData() {
		return data;
	}
	
	public void setData(Object[] data) {
		this.data = data;
	}
}
