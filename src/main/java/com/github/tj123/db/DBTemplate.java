package com.github.tj123.db;

import com.github.tj123.bean.Dto;
import com.github.tj123.bean.Po;
import com.github.tj123.db.exception.DBException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	 * 获取主键 field
	 *
	 * @param clazz
	 * @return
	 */
	protected Field getPrimaryKeyField(Class<? extends Po> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.getAnnotation(PrimaryKey.class) != null) {
				return field;
			}
		}
		return null;
	}
	
	protected String getFieldColumn(Field field){
		Column column = field.getAnnotation(Column.class);
		return column == null ? field.getName() : column.value();
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
		Field field = getPrimaryKeyField(poClass);
		if (field == null) {
			log.debug("@PrimaryKey 没有找到！", new Exception("@PrimaryKey 没有找到！"));
			return "";
		}
		field.setAccessible(true);
		String primaryKey = String.valueOf(field.get(po));
		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(getDataSource()).withTableName(table.value());
		if (primaryKey == null || primaryKey.trim().equals("") || "null".equals(primaryKey)) {
			return String.valueOf(simpleJdbcInsert.usingGeneratedKeyColumns(getFieldColumn(field))
					.executeAndReturnKeyHolder(DBUtil.poToMap(po, false)).getKey());
		}
		simpleJdbcInsert.execute(DBUtil.poToMap(po, false));
		return primaryKey;
	}
	
	/**
	 * 只对同一张表批量插入
	 * <p/>(无法重载)
	 * @param dtos
	 */
	public <DTO extends Dto> void saveDtos(List<DTO> dtos) throws Exception{
		List<Po> list = new ArrayList<>();
		for (DTO dto : dtos) {
			list.add(dto.toPo());
		}
		save(list);
	}
	
	/**
	 * 只对同一张表批量插入
	 * @param pos
	 */
	public <PO extends Po> void save(List<PO> pos) throws Exception {
		if(pos == null || pos.size() == 0) return;
		Class<? extends Po> poClass = pos.get(0).getClass();
		Table table = poClass.getAnnotation(Table.class);
		if (table == null) {
			log.debug("@Table 没有找到！", new Exception("@Table 没有找到！"));
			return;
		}
		Field field = getPrimaryKeyField(poClass);
		if (field == null) {
			log.debug("@PrimaryKey 没有找到！", new Exception("@PrimaryKey 没有找到！"));
			return;
		}
		field.setAccessible(true);
		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(getDataSource()).withTableName(table.value());
		boolean genKey = false;
		for (Po po : pos) {
			String primaryKey = String.valueOf(field.get(po));
			if (primaryKey == null || primaryKey.trim().equals("") || "null".equals(primaryKey)) {
				genKey = true;
				break;
			}
		}
		List<Map<String,Object>> list = new ArrayList<>();
		for (Po po : pos) {
			list.add(DBUtil.poToMap(po));
		}
		if(genKey){
			simpleJdbcInsert.usingGeneratedKeyColumns(getFieldColumn(field));
		}
		simpleJdbcInsert.executeBatch(list.toArray(new HashMap[]{}));
	}
	
	/**
	 * 默认依据主键修改
	 *
	 * @param dto
	 * @return
	 * @throws Exception
	 */
	public int update(Dto dto) throws Exception {
		return update(dto, null);
	}
	
	public int update(Dto dto, String... columns) throws Exception {
		return update(dto.toPo(), columns);
	}
	
	/**
	 * 默认依据主键修改
	 *
	 * @param po
	 * @return
	 * @throws Exception
	 */
	public int update(Po po) throws Exception {
		return update(po, null);
	}
	
	/**
	 * 必须保证 po 列 columns 中的值不为 null！
	 *
	 * @param po
	 * @param columns
	 * @return
	 * @throws Exception
	 */
	public int update(Po po, String... columns) throws Exception {
		Class<? extends Po> poClass = po.getClass();
		Table table = poClass.getAnnotation(Table.class);
		if (table == null) {
			log.debug("@Table 没有找到！", new Exception("@Table 没有找到！"));
			return 0;
		}
		Map<String, Object> map = DBUtil.poToMap(po);
		if (columns == null) {
			Field field = getPrimaryKeyField(poClass);
			if (field == null) {
				log.debug("@PrimaryKey 没有找到！", new Exception("@PrimaryKey 没有找到！"));
				return 0;
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
			if (value == null) {
				log.debug("列" + column + "值为空！", new Exception("列" + column + "值为空！"));
				return 0;
			}
			where.put(column, value);
			map.remove(column);
		}
		Sql sql = genUpdateSql(table.value(), map, where);
		return update(sql.getSql(), sql.getData());
	}
	
	abstract Sql genUpdateSql(String table, Map<String, Object> data, Map<String, Object> where) throws Exception;
	
	/**
	 * update insert 存在更新
	 * <p>不存在 插入
	 */
	public void upsert(Dto dto, String keys) throws Exception {
		upsert(dto.toPo(), keys);
	}
	
	public void upsert(Dto dto, String... keys) throws Exception {
		upsert(dto.toPo(), keys);
	}
	
	public abstract void upsert(Po po, String... keys) throws Exception;
	
	public PageResult findPage(String sql, Page page, Object... params) throws Exception {
		PageResult result = new PageResult();
		long start = System.currentTimeMillis();
		if (page.getSize() == 0) {
			throw new DBException("size 不能为 0！");
		}
		page.setTotalRecords(queryForObject(new StringBuffer().append("select count(1) from (").append(sql)
				.append(") cnt").toString(), Long.class, params));
		page.calculatePage();
		Sql sq = genPageSql(sql, page, params);
		result.setPage(page);
		result.setRows(queryForList(sq.getSql(), sq.getData()));
		return result;
	}
	
	abstract Sql genPageSql(String sql, Page page, Object[] params) throws Exception;
}
