package com.github.tj123.db;

import java.util.Map;

/**
 * Created by TJ on 2016/10/20.
 */
public interface Po<DTO extends Dto> extends Bean{
	
	DTO toDto() throws Exception;
	
}
