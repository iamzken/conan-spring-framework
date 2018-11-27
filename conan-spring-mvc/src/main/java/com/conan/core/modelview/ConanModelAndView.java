package com.conan.core.modelview;

import java.io.File;
import java.util.Map;

/**
 * @author: zhangkenan
 * @description: 自定义ModelAndView，为模板提供数据
 * @created: 2018-11-27 17:56
 **/
public class ConanModelAndView {

    private Map<String, Object> model;
    private String view;

    /**
     * 在controller层返回时调用此构造方法
     * @param model
     * @param view
     */
    public ConanModelAndView(Map<String, Object> model, String view) {
        this.model = model;
        this.view = view;
    }

    /**
     * 使用viewResolver解析时调用
     * @return
     */
    public Map<String, Object> getModel() {
        return model;
    }

    /**
     * 使用viewResolver解析时调用
     * @return
     */
    public String getView() {
        return view;
    }
}
