package com.conan.core.controller;

import com.conan.core.annotation.ConanAutowired;
import com.conan.core.annotation.ConanController;
import com.conan.core.annotation.ConanRequestMapping;
import com.conan.core.service.ConanService2;

/**
 * @author: zhangkenan
 * @description: 控制器2
 * @created: 2018-11-09 17:58
 **/
@ConanController
@ConanRequestMapping("/conan2")
public class ConanController2 {

    @ConanAutowired
    private ConanService2 conanService2;

    @ConanRequestMapping("/add")
    public int add(){

        return conanService2.add();
    }

    @ConanRequestMapping("/delete")
    public int delete(){

        return conanService2.delete();
    }

    @ConanRequestMapping("/update")
    public int update(){

        return conanService2.update();
    }

    @ConanRequestMapping("/search")
    public String search(){

        return conanService2.search();
    }
}
