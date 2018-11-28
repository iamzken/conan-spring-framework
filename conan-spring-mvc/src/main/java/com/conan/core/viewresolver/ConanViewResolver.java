package com.conan.core.viewresolver;

import com.conan.core.exception.ConanApplicationContextResolverException;
import com.conan.core.modelview.ConanModelAndView;

import java.io.*;
import java.util.Map;

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

    /**
     * 根据传递的ModelAndView，将对应的模板里的特定字符串进行替换
     * 将文件中的数据一次性读出
     */
    public Object resolve(Map<String, Object> model) throws ConanApplicationContextResolverException {
        //获取文件长度 此种方法
        Long length = templateFile.length();
        //建立字节缓冲区
        byte[] bytes = new byte[length.intValue()];
        try {
            FileInputStream stream = new FileInputStream(templateFile);
            //将读出的文件内容存入字节缓冲区
            stream.read(bytes);
            stream.close();
            String fileContent = new String(bytes);
            for (Map.Entry<String, Object> entry : model.entrySet()) {
                //此处使用正则表达式进行字符串替换   注意：左大括号需要转义，右大括号不需要
                fileContent = fileContent.replaceAll("\\$\\{" + entry.getKey() + "}", entry.getValue().toString());
            }
            return fileContent;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConanApplicationContextResolverException("视图解析器解析异常" + e);
        }
    }

    public ConanViewResolver(File templateFile) {
        this.templateFile = templateFile;
    }
}
