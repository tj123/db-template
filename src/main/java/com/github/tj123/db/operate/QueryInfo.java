package com.github.tj123.db.operate;

import com.github.tj123.bean.Po;
import com.github.tj123.db.Column;
import com.github.tj123.db.DBUtil;
import com.github.tj123.db.Table;
import com.github.tj123.db.exception.DBException;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 查詢條件
 */
public class QueryInfo {

    public QueryInfo(Po<?> po) throws Exception {
        this(po,null);
    }

    public QueryInfo(Po<?> po, String... params) throws Exception {
        this.po = po;
        this.params = params;
        Class<? extends Po> poClass = po.getClass();
        Table table = poClass.getAnnotation(Table.class);
        if (table == null) {
            throw new DBException("@Table 没有找到！");
        }
        this.table = table.value();
        Map<String, Object> map = DBUtil.poToMap(po);
        Field field = Util.getPrimaryKeyField(poClass);
        if (field == null) {
            throw new DBException("@PrimaryKey 没有找到！");
        }
        primaryKey = field.getName();
        Column column = field.getAnnotation(Column.class);
        if (column != null) {
            primaryKey = column.value();
        }
        if (params == null) {
            params = new String[]{primaryKey};
        }
        this.params = params;
        data = DBUtil.poToMap(po);
    }

    private Map<String,Object> data;
    private Po<?> po;
    private String[] params;
    private String table;
    private String primaryKey;

    /**
     * 获取查询条件
     * @return
     */
    public String getTable() {
        return table;
    }

    /**
     * 获取
     * @return
     */
    public String[] getParams() {
        return params;
    }

    /**
     * 獲取主鍵
     */
    public String getPrimaryKey() {
        return primaryKey;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
