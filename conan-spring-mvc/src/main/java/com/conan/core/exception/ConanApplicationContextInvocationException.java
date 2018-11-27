package com.conan.core.exception;

/**
 * @author: zhangkenan
 * @description: spring调用异常封装类
 * @created: 2018-11-27 16:23
 **/
public class ConanApplicationContextInvocationException extends Throwable {
    public ConanApplicationContextInvocationException(String s) {
        super(s);
    }
}
