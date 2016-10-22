package com.github.tj123.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TJ on 2016/10/19.
 */
public abstract class DBTemplate extends JdbcTemplate {
	
	private Log log = LogFactory.getLog(DBTemplate.class);
	
	/**
	 * 是否显示日志
	 */
	private boolean showLog = false;
	
	public void setShowLog(boolean showLog) {
		this.showLog = showLog;
	}
	
	public String save(Dto dto) throws Exception {
		return save(dto.toPo());
	}
	
	/**
	 * 默认使用自己生成
	 *
	 * @param po
	 * @return
	 * @throws Exception
	 */
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
		String primaryKey = String.valueOf(field.get(po));
		if (primaryKey == null || primaryKey.trim().equals("") || "null".equals(primaryKey)) {
			return String.valueOf(new SimpleJdbcInsert(getDataSource()).withTableName(table.value())
					.usingGeneratedKeyColumns(field.getName()).executeAndReturnKeyHolder(po.toMap()).getKey());
		}
		new SimpleJdbcInsert(getDataSource()).withTableName(table.value()).execute(po.toMap());
		return primaryKey;
	}
	
	public void update(Dto dto, String... columns) throws Exception {
		update(dto.toPo(), columns);
	}
	
	public void update(Po po, String... columns) throws Exception {
		Class<? extends Po> poClass = po.getClass();
		Table table = poClass.getAnnotation(Table.class);
		if (table == null) {
			log.debug("@Table 没有找到！", new Exception("@Table 没有找到！"));
			return;
		}
		Map<String, Object> map = po.toMap();
		if (columns == null) {
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
				return;
			}
			String primaryKey = field.getName();
			Column column = field.getAnnotation(Column.class);
			if (column != null) {
				primaryKey = column.value();
			}
			columns = new String[]{primaryKey};
		}
		Map<String, Object> where = new HashMap<>();
		for (String column : columns) {
			Object value = map.get(column);
			where.put(column, value);
			map.remove(column);
		}
	}
	
	/**
	 * update insert 存在更新
	 * <p>不存在 插入
	 */
	public void upsert(Dto dto) throws Exception {
		upsert(dto.toPo());
	}
	
	public void upsert(Po po) throws Exception {
		upsert(po, "guid");
	}
	
	public void upsert(Dto dto, String keys) throws Exception {
		upsert(dto.toPo(), keys);
	}
	
	public abstract void upsert(Po po, String... keys) throws Exception;
	
	public abstract PageResult findPage(String sql, Page page, Object... params) throws Exception;
}
