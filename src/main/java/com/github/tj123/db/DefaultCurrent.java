package com.github.tj123.db;

/**
 * Created by TJ on 2016/10/24.
 */

import java.lang.annotation.*;
import java.util.Date;

/**
 * 如果打了注解的 为 null 时 直接赋值为当前时间
 * @author TJ
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DefaultCurrent {
}
