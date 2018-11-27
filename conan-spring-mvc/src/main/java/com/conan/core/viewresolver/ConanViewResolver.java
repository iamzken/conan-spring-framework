package com.conan.core.viewresolver;

import com.conan.core.modelview.ConanModelAndView;

import java.io.File;

/**
 * @author: zhangkenan
 * @description: 自定义视图解析器，将${xxx}替换成具体值
 * @created: 2018-11-27 18:18
 **/
public class ConanViewResolver {
    /**
     * 模板文件
     */
    private File templateFile;

    //TODO
    public Object resolve(ConanModelAndView modelAndView) {
        return null;
    }

    public ConanViewResolver(File templateFile) {
        this.templateFile = templateFile;
    }
}
