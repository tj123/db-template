package com.github.tj123.db.operate;

import java.util.*;

/**
 * Where 条件查询语句\
 * 生成的语句前面有空格
 */
public class WhereEquals implements DBSql{

    public WhereEquals(Map<String,Object> map){
        this.map = map;
    }

    private Map<String, Object> map;

    public Sql getMysql() throws Exception {
        Sql sql = new Sql();
        StringBuilder sb = new StringBuilder();
        List<Object> list = new ArrayList<>();
        Set<Map.Entry<String, Object>> entries = map.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            Object value = entry.getValue();
            if (value instanceof Date) {
                sb.append("`").append(entry.getKey()).append("` = date_format(?,'%Y-%m-%d %H:%i:%s') and ");
                list.add(Util.dateToString((Date) value, "yyyy-MM-dd HH:mm:ss"));
            } else {
                sb.append("`" + entry.getKey() + "` = ? and ");
                list.add(value);
            }
        }
        if (entries.size() > 0) {
            int length = sb.length();
            sb.delete(length - 5, length);
        }
        sql.setContent(sb);
        sql.setParams(list);
        return sql;
    }

    public Sql getOracle() throws Exception {
        Sql sql = new Sql();
        StringBuilder sb = new StringBuilder();
        List<Object> list = new ArrayList<>();
        Set<Map.Entry<String, Object>> entries = map.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            Object value = entry.getValue();
            if (value instanceof Date) {
                sb.append(entry.getKey()).append(" = to_date(?,'yyyy-mm-dd hh24:mi:ss') and ");
                list.add(Util.dateToString((Date) value, "yyyy-MM-dd HH:mm:ss"));
            } else {
                sb.append(entry.getKey()).append(" = ? and ");
                list.add(value);
            }
        }
        if (entries.size() > 0) {
            int length = sb.length();
            sb.delete(length - 5, length);
        }
        sql.setContent(sb);
        sql.setParams(list);
        return sql;
    }

}
