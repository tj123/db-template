package com.github.tj123.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TJ on 2016/10/20.
 */
public class PageResult extends HashMap<String, Object> {
	
	private static final long serialVersionUID = 1L;
	
	private Page page;
	
	public PageResult setPage(Page page){
		this.page = page;
		put("total",page.getTotal());
		return this;
	}
	
	public Page getPage() {
		return page;
	}
	
	public PageResult setRows(List<Map<String,Object>> rows){
		put("rows",rows);
		return this;
	}
	
	public List<Map<String,Object>> getRows(){
		return (List<Map<String,Object>>) get("rows");
	}
	
	/**
	 * 遍历 rows
	 * @return
	 */
	public PageResult forEach(EachRow<Map<String,Object>> row) throws Exception {
		for(Map<String,Object> map:getRows()){
			row.row(map);
		}
		return this;
	}
	
	
}
