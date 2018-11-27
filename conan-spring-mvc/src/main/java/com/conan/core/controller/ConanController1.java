package com.conan.core.controller;

import com.conan.core.annotation.ConanAutowired;
import com.conan.core.annotation.ConanController;
import com.conan.core.annotation.ConanRequestMapping;
import com.conan.core.annotation.ConanRequestParam;
import com.conan.core.service.ConanService1;

import java.util.regex.Pattern;

/**
 * @author: zhangkenan
 * @description: 控制器1
 * @created: 2018-11-09 17:19
 **/
@ConanController
@ConanRequestMapping("/conan1")
public class ConanController1 {

    @ConanAutowired
    private ConanService1 conanService1;

    /**
     * 加法
     * @param first 第一个加数
     * @param second 第二个加数
     * @param third 第三个加数
     * @return 两个数的和
     */
    @ConanRequestMapping("/add")
    public String add(@ConanRequestParam("fst")String first, @ConanRequestParam("snc")String second, String third){

        return String.valueOf(conanService1.add(Double.valueOf(first), Double.valueOf(second)));
    }

    /**
     * 减法
     * @param first 被减数
     * @param second 减数
     * @return 两个数的差
     */
    @ConanRequestMapping("/minus")
    public String minus(@ConanRequestParam("fst")String first, @ConanRequestParam("snd")String second){

        return String.valueOf(conanService1.minus(Double.valueOf(first), Double.valueOf(second)));
    }

    /**
     * 乘法
     * @param first 第一个乘数
     * @param second 第二个乘数
     * @return 乘积
     */
    @ConanRequestMapping("/multiply")
    public String multiply(@ConanRequestParam("fst")String first, @ConanRequestParam("snd")String second){

        return String.valueOf(conanService1.multiply(Double.valueOf(first), Double.valueOf(second)));
    }

    /**
     * 除法
     * @param first 被除数
     * @param second 除数
     * @return 除法结果
     */
    @ConanRequestMapping("/divide")
    public String divide(@ConanRequestParam("fst")String first, @ConanRequestParam("second")String second){

        String regExp = "([0]+)|[0]+.(0)*";
        Pattern pattern = Pattern.compile(regExp);
        if(pattern.matcher(String.valueOf(second).toString()).find()){
            return "0";
        }
        return String.valueOf(conanService1.divide(Double.valueOf(first), Double.valueOf(second)));
    }

    /**
     * 干扰方法，验证该方法不会被注册为一个Handler
     * @return
     */
    private String test(){
        return "test";
    }
}
