package com.conan.core.annotation;

import java.lang.annotation.*;

/**
 * @author: zhangkenan
 * @description: 自定义service注解
 * @created: 2018-11-09 17:12
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConanService {
    String name() default "";
}
