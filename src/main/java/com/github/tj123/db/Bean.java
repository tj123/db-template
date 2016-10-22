package com.github.tj123.db;

import java.util.Map;

/**
 * Created by TJ on 2016/10/20.
 */
public interface Bean {
	
	String getUUID();
	
	/**
	 * 注解 @Column 起作用
	 * @return
	 * @throws Exception
	 */
	Map<String,Object> toMap() throws Exception;
	
}
