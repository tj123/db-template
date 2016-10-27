package com.github.tj123.db.operate;

/**
 * Created by TJ on 2016/10/27.
 */
public class Param {

    private StringBuilder sql;

    private Object[] params;

    public StringBuilder getSql() {
        return sql;
    }

    public void setSql(StringBuilder sql) {
        this.sql = sql;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
