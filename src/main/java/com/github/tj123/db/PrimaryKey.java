package com.github.tj123.db;

import java.lang.annotation.*;

/**
 * Created by TJ on 2016/10/20.
 * 主键注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PrimaryKey {
}
