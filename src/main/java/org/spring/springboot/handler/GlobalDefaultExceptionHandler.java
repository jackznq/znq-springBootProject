package org.spring.springboot.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 *  全局异常处理
 * Created by ddfhznq on 2017/10/18.
 */
@ControllerAdvice
public class GlobalDefaultExceptionHandler
{
    public void defaultErrorHandler(HttpServletRequest req,Exception e){
        e.printStackTrace();
        System.out.println("GlobalDefaultExceptionHandler.defaultErrorHandler");
    }
}
