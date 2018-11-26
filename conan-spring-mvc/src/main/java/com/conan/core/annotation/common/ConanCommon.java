package com.conan.core.annotation.common;

import java.lang.annotation.*;

/**
 * @author: zhangkenan
 * @description: 公用注解类
 * @created: 2018-11-26 14:53
 **/
@Documented
@Inherited
public @interface ConanCommon {
    boolean lazyInit() default true;
}
