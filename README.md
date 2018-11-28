# conan-spring-framework
手撸spring源码

基于jdk8版本

1、访问地址：http://localhost:8892/conan-spring-mvc/conan3/test001?name=xiaoming&age=20

    返回效果：hell, my name is xiaoming, i am 20 years old !

2、使用自定义解析器解析自定义.co文件

3、服务端返回数据中文乱码问题解决

    访问地址：<http://localhost:8892/conan-spring-mvc/conan3/test001?name=xiaoming&age=20>
    
    返回结果：

    hell, my name is xiaoming, i am 20 years old !
    ------------------------------------------------------------------------------------
    大家好，我的名字是xiaoming, 我今年20岁了！

    访问地址：http://localhost:8892/conan-spring-mvc/conan3/test001?name=%E5%BC%A0%E7%BF%8A%E7%BF%80&age=1
    返回结果：
    hell, my name is 张翊翀, i am 1 years old !
    ------------------------------------------------------------------------------------
    大家好，我的名字是张翊翀, 我今年1岁了！
