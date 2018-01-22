package com.znq.rpc.support.spring;

import com.znq.common.config.MethodConfig;
import com.znq.common.config.ServiceProviderConfig;
import com.znq.common.registry.AbstractServiceDiscovery;
import com.znq.rpc.Forest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;

/**
 * Created by znq on 2016/12/7.
 */
public class ForestProxyFactoryBean implements FactoryBean<Object>, InitializingBean, DisposableBean
{

    private final static Logger LOGGER = LoggerFactory.getLogger(ForestProxyFactoryBean.class);

    private Class<?> serviceInterface;

    private Map<String, MethodConfig> methodConfigMap;

    private Object proxyBean;

    private AbstractServiceDiscovery discovery;

    @Override
    public void destroy() throws Exception {
    }

    @Override
    public Object getObject() throws Exception {
        return proxyBean;
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    public void setMethodConfigMap(Map<String, MethodConfig> methodConfigMap) {
        this.methodConfigMap = methodConfigMap;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ServiceProviderConfig config = ServiceProviderConfig.Builder.newBuilder().build();
        for (Map.Entry<String, MethodConfig> methodConfigEntry : methodConfigMap.entrySet()) {
            config.registerMethodConfig(methodConfigEntry.getKey(), methodConfigEntry.getValue());
        }
        proxyBean = Forest.from(serviceInterface, config, discovery);
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public AbstractServiceDiscovery getDiscovery() {
        return discovery;
    }

    public void setDiscovery(AbstractServiceDiscovery discovery) {
        this.discovery = discovery;
    }
}
