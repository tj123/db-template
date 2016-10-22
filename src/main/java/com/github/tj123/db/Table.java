package com.github.tj123.db;

import java.lang.annotation.*;

/**
 * Created by TJ on 2016/10/20.
 * 表名声明
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
	
	String value();
	
}
