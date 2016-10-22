package com.github.tj123.db;

import java.util.Map;

/**
 * Created by TJ on 2016/10/20.
 */
public interface EachRow<T> {
	
	void row(T row) throws Exception;
	
}
