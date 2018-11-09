package com.conan.core.annotation;

import java.lang.annotation.*;

/**
 * @author: zhangkenan
 * @description: 自定义RequestParam注解
 * @created: 2018-11-09 17:17
 **/
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConanRequestParam {
    String value() default "";
}
