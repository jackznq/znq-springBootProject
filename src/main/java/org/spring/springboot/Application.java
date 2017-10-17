package org.spring.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring Boot 应用启动类
 * <p>
 * Created by ddfhznq on 16/4/26.
 */
// Spring Boot 应用的标识
        @Configuration
        @EnableAutoConfiguration
        @ComponentScan
@SpringBootApplication
public class Application
{

    public static void main(String[] args)
    {
        // 程序启动入口
        // 启动嵌入式的 Tomcat 并初始化 Spring 环境及其各 Spring 组件
        SpringApplication.run(Application.class, args);
        Map map = new HashMap();
        map.put("234", 122);
    }
}