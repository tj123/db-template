package com.github.tj123.db;

import com.github.tj123.bean.Po;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.lang.reflect.Field;

/**
 * Created by TJ on 2016/10/20.
 */
public class MysqlTemplate extends DBTemplate {
	
	private Log log = LogFactory.getLog(DBTemplate.class);
	
	public String save(Po po) throws Exception {
		Class<? extends Po> poClass = po.getClass();
		Table table = poClass.getAnnotation(Table.class);
		if (table == null) {
			log.debug("@Table 没有找到！", new Exception("@Table 没有找到！"));
			return "";
		}
		Field field = null;
		Field[] fields = poClass.getDeclaredFields();
		for (Field fd : fields) {
			if (fd.getAnnotation(PrimaryKey.class) != null) {
				field = fd;
				break;
			}
		}
		if (field == null) {
			log.debug("@PrimaryKey 没有找到！", new Exception("@PrimaryKey 没有找到！"));
			return "";
		}
		field.setAccessible(true);
		String uuid = String.valueOf(field.get(po));
		if (uuid == null || uuid.trim().equals("") || "null".equals(uuid)) {
			field.set(po, uuid = po.getUUID()); //通过java生成主键方式生成主键
		}
		new SimpleJdbcInsert(getDataSource()).withTableName(table.value()).execute(po.toMap());
		return uuid;
	}
	
	@Override
	public void upsert(Po po, String... keys) throws Exception {
		
	}
	
	@Override
	public PageResult findPage(String sql, Page page, Object... params) throws Exception {
		return null;
	}
}
