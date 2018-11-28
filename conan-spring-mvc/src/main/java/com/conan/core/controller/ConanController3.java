package com.conan.core.controller;

import com.conan.core.annotation.ConanController;
import com.conan.core.annotation.ConanRequestMapping;
import com.conan.core.annotation.ConanRequestParam;
import com.conan.core.modelview.ConanModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: zhangkenan
 * @description: 控制器类3------干扰类，验证该类不会被注册为一个controller
 * @created: 2018-11-09 18:10
 **/
@ConanController
@ConanRequestMapping("/conan3")
public class ConanController3 {

    @ConanRequestMapping("/test001")
    public ConanModelAndView test001(@ConanRequestParam("name") String yourName, @ConanRequestParam("age") String yourAge){
        Map<String, Object> model = new HashMap<>();
        model.put("name", yourName);
        model.put("age", yourAge);
        ConanModelAndView mv = new ConanModelAndView(model, "user/user1");
        return mv;
    }
}
