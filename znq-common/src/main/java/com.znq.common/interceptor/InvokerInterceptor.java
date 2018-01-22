package com.znq.common.interceptor;

import java.lang.reflect.Method;

/**
 * Created by ddfhznq on 2016/12/7.
 */
public interface InvokerInterceptor {

    boolean beforeInvoke(Object target, Method method, Object... args);

    Object processInvoke(Object target, Method method, Object... args);

    boolean afterInvoke(Object target, Method method, Object result);
}
