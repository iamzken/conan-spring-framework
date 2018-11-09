package com.conan.core.service;

import com.conan.core.annotation.ConanService;

/**
 * @author: zhangkenan
 * @description: 服务类1
 * @created: 2018-11-09 17:20
 **/
@ConanService
public class ConanService1 {

    public double add(double first, double second){
        return first + second;
    }

    public double minus(double first, double second){
        return first - second;
    }

    public double multiply(double first, double second){
        return first * second;
    }

    public double divide(double first, double second) throws IllegalArgumentException{
        return first / second;
    }
}
