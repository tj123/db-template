package com.github.tj123.db;

import com.github.tj123.bean.Dto;
import com.github.tj123.bean.Po;
import com.github.tj123.db.exception.DBException;
import com.github.tj123.db.operate.QueryInfo;
import com.github.tj123.db.operate.Sql;
import com.github.tj123.db.operate.WhereEquals;
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
 * jdbc 重写类
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
	
	/**
	 * 默认使用自己生成
	 *
	 * @param po
	 * @return
	 * @throws Exception
	 */
	public String save(Po po) throws Exception {
		QueryInfo queryInfo = new QueryInfo(po);
		Field field = queryInfo.getPrimaryField();
		field.setAccessible(true);
		String primaryKey = String.valueOf(field.get(po));
		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(getDataSource()).withTableName(queryInfo.getTable());
		if (primaryKey == null || primaryKey.trim().equals("") || "null".equals(primaryKey)) {
			return String.valueOf(simpleJdbcInsert.usingGeneratedKeyColumns(queryInfo.getPrimaryKey())
					.executeAndReturnKeyHolder(DBUtil.poToMap(po, false)).getKey());
		}
		simpleJdbcInsert.execute(DBUtil.poToMap(po, false));
		return primaryKey;
	}
	
	/**
	 * 只对同一张表批量插入
	 * <p/>(无法重载)
	 *
	 * @param dtos
	 */
	public <DTO extends Dto> void saveDtos(List<DTO> dtos) throws Exception {
		List<Po> list = new ArrayList<>();
		for (DTO dto : dtos) {
			list.add(dto.toPo());
		}
		save(list);
	}
	
	/**
	 * 只对同一张表批量插入
	 *
	 * @param pos
	 */
	public <PO extends Po> void save(List<PO> pos) throws Exception {
		if (pos == null || pos.size() == 0) return;
		QueryInfo queryInfo = new QueryInfo(pos.get(0));
		Field field = queryInfo.getPrimaryField();
		field.setAccessible(true);
		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(getDataSource()).withTableName(queryInfo.getTable());
		boolean genKey = false;
		for (Po po : pos) {
			String primaryKey = String.valueOf(field.get(po));
			if (primaryKey == null || primaryKey.trim().equals("") || "null".equals(primaryKey)) {
				genKey = true;
				break;
			}
		}
		List<Map<String, Object>> list = new ArrayList<>();
		for (Po po : pos) {
			list.add(DBUtil.poToMap(po));
		}
		if (genKey) {
			simpleJdbcInsert.usingGeneratedKeyColumns(queryInfo.getPrimaryKey());
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
		QueryInfo queryInfo = new QueryInfo(po, columns);
		Sql sql = genUpdateSql(queryInfo);
		return update(sql.getSql().toString(), sql.getParams().toArray());
	}
	
	/**
	 * 生成 update 语句
	 * @param queryInfo
	 * @return
	 */
	protected abstract Sql genUpdateSql(QueryInfo queryInfo) throws Exception;
	
	
	/**
	 * update insert 存在更新
	 * <p>不存在 插入
	 */
	public void upsert(Dto dto, String keys) throws Exception {
		upsert(dto.toPo(), keys);
	}
	
	/**
	 * update insert 存在更新
	 * <p>不存在 插入
	 */
	public void upsert(Dto dto, String... keys) throws Exception {
		upsert(dto.toPo(), keys);
	}
	
	/**
	 * 默认以主键更新
	 */
	public void upsert(Po po) throws Exception {
		upsert(po, null);
	}
	
	/**
	 * update insert 存在更新
	 * <p>不存在 插入
	 */
	public void upsert(Po po, String... keys) throws Exception {
		QueryInfo queryInfo = new QueryInfo(po, keys);
		if (findCount(queryInfo.getTable(), queryInfo.getWhereEquals()) > 0) {
			upsert(po,keys);
		} else {
			save(po);
		}
	}
	
	protected abstract int findCount(String table, WhereEquals whereEquals) throws Exception;
	
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
		result.setRows(queryForList(sq.getSql().toString(), sq.getParams()));
		return result;
	}
	
	abstract Sql genPageSql(String sql, Page page, Object[] params) throws Exception;
}
