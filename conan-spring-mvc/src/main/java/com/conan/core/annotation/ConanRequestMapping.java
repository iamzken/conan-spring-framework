package com.conan.core.annotation;

import java.lang.annotation.*;

/**
 * @author: zhangkenan
 * @description: 自定义RequestMapping注解
 * @created: 2018-11-09 17:14
 **/
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConanRequestMapping {
    String value() default "";
}
