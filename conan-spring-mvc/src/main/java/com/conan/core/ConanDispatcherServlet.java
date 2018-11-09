package com.conan.core;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @author: zhangkenan
 * @description: 自定义DispatcherServlet
 * @created: 2018-11-09 16:17
 **/
@WebServlet(name = "conanDispatcherServlet", urlPatterns = "/*", loadOnStartup = 0,
        initParams = {@WebInitParam(name = "contextConfigLocation", value = "application.properties")})
public class ConanDispatcherServlet extends HttpServlet {

    /**
     * conan-sprin-mvc配置文件，所有的配置信息都被加载到这个成员变量中
     */
    private Properties properties = new Properties();

    /**
     * 所有被@ConanController和@ConanService标注的类名称，以class类的首字母小写保存
     */
    private List<String> beanNameList = new ArrayList<String>();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    /**
     * 这就是传说中的ioc容器，其实只是一个map，key为beanNameList中保存的beanName，value为对应类的一个实例对象
     */
    private Map<String, Object> iocContainer = new HashMap<String, Object>();

    /**
     * 这就是传说中的handlerMapping，其实也是一个map，key为controller上的@ConanRequestMapping的值和方法上的@ConanRequestMapping
     * 的值确定的唯一路径，value为对应的Method对象
     */
    private Map<String, Method> handlerMapping = new HashMap<String, Method>();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        writer.write("what the fuck!");
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        //conan-spring-mvc配置文件
        String configFile = config.getInitParameter("contextConfigLocation");

        //1、加载配置文件到properties对象中
        doLoadConfigFile(configFile);
        //2、从配置文件中获取扫描路径basePackage
        String basePackage = properties.getProperty("basePackage");
        URL url = this.getClass().getClassLoader().getResource(basePackage.replaceAll(".", "/"));
        //TODO
    }

    /**
     * 加载conan-spring-mvc配置文件
     * @param configFile 配置文件
     */
    private void doLoadConfigFile(String configFile) throws ServletException {
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream(configFile));
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }
}
