package org.spring.springboot.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * hello world 热部署 ctrl + alt+ shift +/
 * Created by ddfhznq on 2017/10/17.
 */
@RestController
@EnableAutoConfiguration
public class SimpleController
{
    @RequestMapping("/")
    @ResponseBody
    String home()
    {
        return "Hello333 world!";
    }

    public static void main(String[] args) throws Exception
    {
        SpringApplication.run(SimpleController.class, args);
    }
}
