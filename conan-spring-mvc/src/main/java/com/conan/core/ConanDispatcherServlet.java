package com.conan.core;

import com.conan.core.annotation.*;
import com.conan.core.exception.ConanApplicationContextInitializationException;

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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
     * 使用ConcurrentHashMap确保线程安全
     */
    private Map<String, Object> iocContainer = new ConcurrentHashMap<String, Object>();

    /**
     * 这就是传说中的handlerMapping，其实也是一个map，key为controller上的@ConanRequestMapping的值和方法上的@ConanRequestMapping
     * 的值确定的唯一路径，value为对应的Method对象
     * 使用ConcurrentHashMap确保线程安全
     */
    private Map<String, Handler> uriHandlerMapping = new ConcurrentHashMap<String, Handler>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI().replace(req.getContextPath(), "");
        Handler handler = uriHandlerMapping.get(uri);
        if(handler == null){
            resp.getWriter().write("404 - Not Found");
            return;
        }
        handler.handle(req, resp);
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
        //4、自动装配
        autowire(iocContainer);
        //5、初始化UrlHandlerMapping
        initUriHandlerMapping(iocContainer);

    }

    /**
     * 初始化UrlHandlerMapping，实质就是将controller的requestMapping+method的requestMapping和instance+method的对应关系保存起来，即url--->handler
     * 只在controller层进行此操作
     * @param iocContainer
     */
    private void initUriHandlerMapping(Map<String, Object> iocContainer) {
        iocContainer.forEach((key, value) -> {
            Class<?> clazz = value.getClass();
            if(clazz.isAnnotationPresent(ConanController.class)){
                //获取该bean的所有public方法，包括从父类继承过来的public方法
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    StringBuffer uri = new StringBuffer();
                    if(clazz.isAnnotationPresent(ConanRequestMapping.class)){
                        uri.append(clazz.getAnnotation(ConanRequestMapping.class).value());
                    }
                    if(method.isAnnotationPresent(ConanRequestMapping.class)){
                        uri.append(method.getAnnotation(ConanRequestMapping.class).value());
                        uriHandlerMapping.put(uri.toString(), new Handler(value, method));
                    }
                }
            }
        });
    }

    /**
     * 对IOC容器中的bean进行自动装配
     * @param iocContainer
     */
    private void autowire(Map<String, Object> iocContainer) {

        if(iocContainer == null || iocContainer.size() == 0){
            return;
        }
        for (Map.Entry<String, Object> entry : iocContainer.entrySet()) {
            Object bean = entry.getValue();
            Class<?> clazz = bean.getClass();
            //获取本类所有字段信息，包括private字段，但不包括父类继承的字段
            for(Field field : clazz.getDeclaredFields()){
                if(field.isAnnotationPresent(ConanAutowired.class)) {
                    field.setAccessible(true);
                    ConanAutowired conanAutowired = field.getAnnotation(ConanAutowired.class);
                    String key = conanAutowired.value().trim();
                    //默认按照name注入
                    if (!"".equals(key)){
                        try {
                            field.set(bean, iocContainer.get(key));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    //name不存在，则以className首字母小写为key进行注入，如果也不匹配，则按照type注入，如果还不匹配，则抛出异常
                    }else{
                        key = field.getName();
                        Object o = iocContainer.get(key);
                        if(o == null){
                            o = iocContainer.get(field.getType().getName());
                            if(o == null){
                                throw new ConanApplicationContextInitializationException("spring容器初始化失败，字段" + clazz.getName() + "." +field.getName() + "无法注入，因为没有找到合适的注入对象");
                            }
                        }
                        try {
                            field.set(bean, o);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
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
                    saveIOCItem(clazz, serviceName, instance);
                //对所有标注了ConanController的类进行处理
                }else if(clazz.isAnnotationPresent(ConanController.class)){
                    ConanController conanController = clazz.getAnnotation(ConanController.class);
                    //忽略空格
                    String controllerName = conanController.name().trim();
                    Object instance = clazz.newInstance();
                    saveIOCItem(clazz, controllerName, instance);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 抽取公用方法，提高代码复用率
     * @param clazz
     * @param annotationValue 获取的注解name值
     * @param instance
     */
    private void saveIOCItem(Class<?> clazz, String annotationValue, Object instance) {
        if(!"".equals(annotationValue)){
            iocContainer.put(annotationValue, instance);
        }else{
            //controller默认以className首字母小写之后的字符串为key
            String name = clazz.getSimpleName();
            //将字符串首字母进行小写处理
            char[] simpleNameCharArray = name.toCharArray();
            simpleNameCharArray[0] += 32;
            name = String.valueOf(simpleNameCharArray);
            if(clazz.isAnnotationPresent(ConanService.class)){
                //如果是service则以className全称为key，按类型注入时使用
                name = clazz.getName();
            }

            iocContainer.put(name, instance);
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

    /**
     * 自定义内部类，封装instance和method，作为一个handler
     */
    private class Handler {

        private Object object;
        private Method method;

        public Handler(Object object, Method method) {
            this.object = object;
            this.method = method;
        }

        /**
         * 根据浏览器传过来的请求参数进行组装并通过反射调用相应的method
         * 本版本只支持参数类型为String的handler，其他类型在随后的版本中会逐步实现
         * @param request
         */
        public void handle(HttpServletRequest request, HttpServletResponse response) {
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            Map<String, String[]> parameterMap = request.getParameterMap();
            Object[] argValues = new Object[parameterAnnotations.length];
            for (int i = 0; i < parameterAnnotations.length; i++){
                Annotation[] annotations = parameterAnnotations[i];
                for(Annotation annotation : annotations){
                    if(annotation instanceof ConanRequestParam){
                        String name = ((ConanRequestParam) annotation).value();
                        //本版本一律以字符串处理
                        argValues[i] = castToString(parameterMap.get(name));
                    }
                }
            }
            try {
                Object result = method.invoke(object, argValues);
                //此处将method的返回结果进行简单的输出
                response.getWriter().write(result.toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Object castToString(String[] strings) {

            StringBuffer result = new StringBuffer();
            for(int i = 0; i< strings.length; i++){
                result.append(strings[i]);
            }
            return result.toString();
        }
    }
}
