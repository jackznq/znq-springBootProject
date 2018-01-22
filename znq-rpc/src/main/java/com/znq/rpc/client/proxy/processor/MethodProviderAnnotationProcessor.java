package com.znq.rpc.client.proxy.processor;

import com.google.common.base.Strings;
import com.znq.common.annotation.MethodProvider;
import com.znq.common.config.MethodConfig;
import com.znq.common.config.ServiceProviderConfig;

import java.lang.reflect.Method;

/**
 * Created by znq on 2016/12/7.
 */
public class MethodProviderAnnotationProcessor extends AbstractAnnotationProcessor {


    @Override
    public void process(String serviceName, Method method, ServiceProviderConfig config) {
        MethodProvider methodProvider = method.getAnnotation(MethodProvider.class);
        if (methodProvider != null) {
            String methodName = Strings.isNullOrEmpty(methodProvider.methodName()) ? method.getName() : methodProvider.methodName();

            MethodConfig methodConfig = MethodConfig.Builder.newBuilder()
                    .withSerializeType(methodProvider.serializeType())
                    .withTimeout(methodProvider.timeout())
                    .withCompressType(methodProvider.compressType())
                    .build();

            methodConfig.setServiceName(serviceName);
            methodConfig.setMethodName(methodName);
            //
            config.registerMethodConfig(methodName, methodConfig);

        }

    }
}
