package com.conan.core;

import com.conan.core.annotation.ConanController;
import com.conan.core.annotation.ConanService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @author: zhangkenan
 * @description: 自定义DispatcherServlet
 * @created: 2018-11-09 16:17
 **/
@WebServlet(name = "conanDispatcherServlet", urlPatterns = "/*", loadOnStartup = 0, initParams = {@WebInitParam(name = "contextConfigLocation", value = "application.properties")})
public class ConanDispatcherServlet extends HttpServlet {

    /**
     * conan-sprin-mvc配置文件，所有的配置信息都被加载到这个成员变量中
     */
    private Properties properties = new Properties();

    /**
     * 所有被@ConanController和@ConanService标注的类名称，以class类的首字母小写保存
     */
    private List<String> beanNameList = new ArrayList<String>();

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

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

        //1、加载配置文件到properties对象中
        loadConfigFile(config.getInitParameter("contextConfigLocation"));
        //2、从配置文件中获取扫描路径basePackage，并将扫描到的所有bean对象的全路径列表进行保存
        loadBeanNameList(properties.getProperty("basePackage"));
        //3、初始化ioc容器
        initIocContainer(beanNameList);
        //4、自动装配 TODO
        autowire(iocContainer);

    }

    /**
     * 对IOC容器中的bean进行自动装配
     * @param iocContainer
     */
    private void autowire(Map<String, Object> iocContainer) {

        if(iocContainer == null || iocContainer.size() == 0){
            return;
        }
        for (Map.Entry entry : iocContainer.entrySet()) {
            
        }
    }

    /**
     * 对扫描路径下的所有bean进行过滤和注册
     * 之所以单独将此步骤独立出来，而不在loadBeanNameList方法中实现，是为了方法的职责更加清晰
     * @param beanNameList 扫描路径下的所有bean的name列表
     */
    private void initIocContainer(List<String> beanNameList) {
        if(beanNameList == null || beanNameList.size() == 0){
            return;
        }
        for (String beanName : beanNameList) {
            try {
                //此处要用?号通配符泛型
                Class<?> clazz = Class.forName(beanName);
                //对所有标注了ConanService的类进行处理
                if(clazz.isAnnotationPresent(ConanService.class)){
                    ConanService conanService = clazz.getAnnotation(ConanService.class);
                    //忽略空格
                    String serviceName = conanService.name().trim();
                    Object instance = clazz.newInstance();
                    //ioc容器保存两份数据，一部分是name和instance的对应关系，如下：
                    saveIOCItem(clazz, serviceName, instance);
                    //另一部分是class类全称和instance的对应关系，按类型注入时使用
                    iocContainer.put(beanName, instance);
                //对所有标注了ConanController的类进行处理
                }else if(clazz.isAnnotationPresent(ConanController.class)){
                    ConanController conanCOntroller = clazz.getAnnotation(ConanController.class);
                    //忽略空格
                    String serviceName = conanCOntroller.name().trim();
                    Object instance = clazz.newInstance();
                    saveIOCItem(clazz, serviceName, instance);
                    //另一部分是class类全称和instance的对应关系，按类型注入时使用
                    iocContainer.put(beanName, instance);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 抽取公用方法，提高代码复用率
     * @param clazz
     * @param annotationName 获取的注解name值
     * @param instance
     */
    private void saveIOCItem(Class<?> clazz, String annotationName, Object instance) {
        //ioc容器保存两份数据，一部分是name和instance的对应关系，如下：
        if(!"".equals(annotationName)){
            iocContainer.put(annotationName, instance);
        }else{
            //默认以className首字母小写之后的字符串为key
            String simpleName = clazz.getSimpleName();
            //将字符串首字母进行小写处理
            char[] simpleNameCharArray = simpleName.toCharArray();
            simpleNameCharArray[0] += 32;
            iocContainer.put(String.valueOf(simpleNameCharArray), instance);
        }
    }

    /**
     * 获取给定路径下的所有class类全称列表
     * 递归调用
     * @param basePackage
     */
    private void loadBeanNameList(String basePackage){
        URL url = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "\\" + File.separator));
        String path = url.getPath();
        File[] fileArray =  new File(path).listFiles();
        for(int i = 0; i < fileArray.length; i++){
            File file = fileArray[i];
            String fileName = file.getName();
            if(file.isDirectory()){
                loadBeanNameList(basePackage + "." + fileName);
            }else if(fileName.endsWith(".class")){
                beanNameList.add(basePackage + "." + fileName.substring(0, (fileName.length() - 6)));
            }
        }
    }

    /**
     * 加载conan-spring-mvc配置文件
     * @param configFile 配置文件
     */
    private void loadConfigFile(String configFile) throws ServletException {
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream(configFile));
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }
}
