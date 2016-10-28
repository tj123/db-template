package com.github.tj123.db.operate;

import com.github.tj123.bean.Po;
import com.github.tj123.db.Column;
import com.github.tj123.db.DBUtil;
import com.github.tj123.db.Table;
import com.github.tj123.db.exception.DBException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 查询条件
 */
public class QueryInfo {

    public QueryInfo(Po<?> po) throws Exception {
        this(po,null);
    }

    public QueryInfo(Po<?> po, String... params) throws Exception {
        this.params = params;
        Class<? extends Po> poClass = po.getClass();
        Table table = poClass.getAnnotation(Table.class);
        if (table == null) {
            throw new DBException("@Table 没有找到！");
        }
        this.table = table.value();
        field = Util.getPrimaryKeyField(poClass);
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
    private String[] params;
    private String table;
    private String primaryKey;
    private Field field;
    
    /**
     * 获取主键
     * @return
     */
    public Field getPrimaryField() {
        return field;
    }
    
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
     * 获取主键
     */
    public String getPrimaryKey() {
        return primaryKey;
    }

    public Map<String, Object> getData() {
        return data;
    }
    
    /**
     * 注意 当调用了此方法后 将会在data中移除与 params 相匹配的元素
     * @return
     * @throws Exception
     */
    public WhereEquals getWhereEquals() throws Exception {
        Map<String,Object> where = new HashMap<>();
        for (String param : params) {
            Object value = data.get(param);
            if (value == null) {
                throw new DBException("条件" + param + "为空");
            }
            where.put(param,value);
            data.remove(param);
        }
        return new WhereEquals(where);
    }
}
