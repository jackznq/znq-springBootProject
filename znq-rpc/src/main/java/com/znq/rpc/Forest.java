package com.znq.rpc;

import com.znq.common.config.ServiceProviderConfig;
import com.znq.common.registry.AbstractServiceDiscovery;
import com.znq.rpc.client.cluster.FailoverCheckingStrategy;
import com.znq.rpc.client.proxy.ForestDynamicProxy;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by znq on 2016/12/7.
 */
public class Forest {

    private final static Logger LOGGER = LoggerFactory.getLogger(Forest.class);

    public static <T> T from(Class<T> clazz) throws Exception {
        return ForestDynamicProxy.newInstance(clazz, null, AbstractServiceDiscovery.DEFAULT_DISCOVERY, new FailoverCheckingStrategy<>());
    }

    public static <T> T from(Class<T> clazz, ServiceProviderConfig config) throws Exception {
        return ForestDynamicProxy.newInstance(clazz, config, AbstractServiceDiscovery.DEFAULT_DISCOVERY, new FailoverCheckingStrategy());
    }

    public static <T> T from(Class<T> clazz, ServiceProviderConfig config, AbstractServiceDiscovery discovery) throws Exception {
        return ForestDynamicProxy.newInstance(clazz, config, discovery, new FailoverCheckingStrategy());
    }

    public static <T> T from(Class<T> clazz, ServiceProviderConfig config, AbstractServiceDiscovery discovery, GenericKeyedObjectPoolConfig keyedObjectPoolConfig) throws Exception {
        return ForestDynamicProxy.newInstance(clazz, config, discovery, new FailoverCheckingStrategy(), keyedObjectPoolConfig);
    }
}
