package com.github.tj123.db.operate;

import com.github.tj123.db.*;

import java.util.*;

/**
 * Created by TJ on 2016/10/27.
 */
public class WhereCondition {

    public WhereCondition(Map<String,Object> map){
        this.map = map;
    }

    private Map<String, Object> map;

    public Param getMysql() throws Exception {
        Param param = new Param();
        StringBuilder sql = new StringBuilder();
        List<Object> list = new ArrayList<>();
        Set<Map.Entry<String, Object>> entries = map.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            Object value = entry.getValue();
            if (value instanceof Date) {
                sql.append("`" + entry.getKey() + "` = date_format(?,'%Y-%m-%d %H:%i:%s') and");
                list.add(Util.dateToString((Date) value, "yyyy-MM-dd HH:mm:ss"));
            } else {
                sql.append(" `" + entry.getKey() + "` = ? and");
                list.add(value);
            }
        }
        if (entries.size() > 0) {
            int length = sql.length();
            sql.delete(length - 4, length);
        }
        param.setSql(sql);
        param.setParams(list.toArray());
        return param;
    }

    public StringBuilder getOracle(){
        return null;
    }

}
