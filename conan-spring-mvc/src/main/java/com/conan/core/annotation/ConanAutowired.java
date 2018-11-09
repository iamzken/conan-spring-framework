package com.conan.core.annotation;

import java.lang.annotation.*;

/**
 * @author: zhangkenan
 * @description: 自定义Autowired注解
 * @created: 2018-11-09 17:32
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConanAutowired {
    String value() default "";
}
