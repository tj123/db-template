package com.github.tj123.db;

import java.lang.annotation.*;

/**
 * Created by TJ on 2016/10/20.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Column {
	
	String value();
	
}
