package org.spring.springboot.controller;

import org.spring.springboot.dao.domain.Demo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 返回json数据
 * Created by ddfhznq on 2017/10/18.
 */
@RestController
@RequestMapping("/demo")
public class DemoController
{
    @RequestMapping("/getFastJson")
    public Demo getDemo()
    {
        Demo demo = new Demo();
        demo.setId(1L);
        demo.setName("znq");
        return demo;
    }

    @RequestMapping("/zeroException")
    public int zeroException()
    {
        return 100 / 0;
    }
}
