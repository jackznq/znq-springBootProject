package com.znq.rpc.client.proxy.processor;


import com.znq.common.config.ServiceExportConfig;
import com.znq.common.config.ServiceProviderConfig;

import java.lang.reflect.Method;

/**
 * Created by znq on 2016/12/7.
 */
public interface IAnnotationProcessor {

    void process(String serviceName, Method method, ServiceProviderConfig config);

    void process(Class<?> serviceClass, Method method, ServiceExportConfig config);
}
