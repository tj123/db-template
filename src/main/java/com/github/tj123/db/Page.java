package com.github.tj123.db;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TJ on 2016/5/19.
 */
public class Page implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final int DEFAULT_SIZE = 20;
	public static final int MAX_SIZE = 100;
	
	/**
	 * 第几页
	 */
	private int page;
	
	/**
	 * 共多少页
	 */
	private int total;
	
	/**
	 * 每页多少
	 */
	private int size;
	
	/**
	 * 排序
	 */
	private String sort;
	
	/**
	 * 关键字
	 */
	private String keyword;
	
	/**
	 * 总的记录数
	 */
	private long totalRecords;
	
	/**
	 * 多条件查询
	 */
	private Map<String, String> keywords = new HashMap<>();
	
	/**
	 * 填充默认参数
	 *
	 * @return
	 */
	public Page deft() {
		if (size <= 0 || size > MAX_SIZE)
			setSize(DEFAULT_SIZE);
		if (sort == null)
			sort = "";
		if (page <= 0)
			page = 1;
		if (keyword == null) {
			keyword = "";
		}
		keyword = keyword.trim().replaceAll("\\s+", "%");
		if (keywords != null && !keywords.isEmpty()) {
			for (String key : keywords.keySet()) {
				String value = keywords.get(key);
				keywords.put(key, (value == null || value.trim().equals("")) ? "" : value.trim().replaceAll("\\s+", "%"));
			}
			
		}
		return this;
	}
	
	/**
	 * 关键字的封装
	 *
	 * @return
	 */
	public Map<String, String> keywords() {
		deft();
		for (String key : keywords.keySet()) {
			String value = keywords.get(key);
			keywords.put(key, (value == null || value.trim().equals("")) ? "%" : "%" + value + "%");
		}
		return getKeywords();
	}
	
	/**
	 * 关键字的封装
	 *
	 * @return
	 */
	public String keyword() {
		deft();
		return "%" + getKeyword() + "%";
	}
	
	/**
	 * 已知总的记录数计算分页条数
	 *
	 * @return
	 */
	public Page calculatePage() {
		this.total = (int) Math.ceil((float) totalRecords / (float) size);
		return this;
	}
	
	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}

	public long getTotalRecords() {
		return totalRecords;
	}

	public int getPage() {
		return page;
	}
	
	public void setPage(int page) {
		this.page = page;
	}
	
	public int getTotal() {
		return total;
	}
	
	public void setTotal(int total) {
		this.total = total;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public String getSort() {
		return sort;
	}
	
	public void setSort(String sort) {
		this.sort = sort;
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public Map<String, String> getKeywords() {
		return keywords;
	}
	
	public void setKeywords(Map<String, String> keywords) {
		this.keywords = keywords;
	}
}
