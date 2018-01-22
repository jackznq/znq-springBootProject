package com.znq.rpc.client.proxy.processor;


import com.znq.common.config.ServiceExportConfig;
import com.znq.common.config.ServiceProviderConfig;

import java.lang.reflect.Method;

/**
 * Created by znq on 2016/12/9.
 */
public abstract class AbstractAnnotationProcessor implements IAnnotationProcessor {

    @Override
    public void process(String serviceName, Method method, ServiceProviderConfig config) {
    }

    @Override
    public void process(Class<?> serviceClass, Method method, ServiceExportConfig config) {

    }
}
