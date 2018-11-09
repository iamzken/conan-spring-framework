package com.conan.core.annotation;

import java.lang.annotation.*;

/**
 * @author: zhangkenan
 * @description: 自定义controller注解
 * @created: 2018-11-09 17:09
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConanController {
    String name() default "";
}
