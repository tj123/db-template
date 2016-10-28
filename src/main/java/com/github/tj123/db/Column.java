package com.github.tj123.db;

import java.lang.annotation.*;

/**
 * 映射到实体表单的列名
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
	
	String value();
	
}
