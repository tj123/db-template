package com.github.tj123.db.operate;


/**
 *  不同数据库的sql语句
 */
public interface DBSql {
	
	Sql getMysql() throws Exception;
	
	Sql getOracle() throws Exception;
}
