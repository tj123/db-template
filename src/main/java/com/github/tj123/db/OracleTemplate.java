package com.github.tj123.db;

import com.github.tj123.bean.Po;

/**
 * Created by TJ on 2016/10/20.
 */
public class OracleTemplate extends DBTemplate {
	
	@Override
	public void upsert(Po po, String... keys) throws Exception {
		
	}
	
	@Override
	public PageResult findPage(String sql, Page page, Object... params) throws Exception {
		return null;
	}
}
