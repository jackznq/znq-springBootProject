package com.znq.rpc.client.proxy;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.znq.common.ServerInfo;
import com.znq.common.annotation.MethodProvider;
import com.znq.common.annotation.ServiceProvider;
import com.znq.common.codec.Header;
import com.znq.common.codec.Message;
import com.znq.common.codec.Request;
import com.znq.common.config.MethodConfig;
import com.znq.common.config.ServiceProviderConfig;
import com.znq.common.exception.ForestFrameworkException;
import com.znq.common.registry.AbstractServiceDiscovery;
import com.znq.common.registry.impl.LocalServiceDiscovery;
import com.znq.rpc.client.cluster.FailoverCheckingStrategy;
import com.znq.rpc.client.cluster.ha.AbstractHAStrategy;
import com.znq.rpc.client.cluster.ha.FailFastStrategy;
import com.znq.rpc.client.cluster.ha.FailoverStrategy;
import com.znq.rpc.client.cluster.lb.AbstractLoadBalance;
import com.znq.rpc.client.cluster.lb.RandomLoadBalance;
import com.znq.rpc.client.proxy.processor.*;
import com.znq.rpc.transport.NettyClient;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Created by znq on 2016/12/7.
 */
public class ForestDynamicProxy implements InvocationHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(ForestDynamicProxy.class);

    private ServiceProviderConfig config;
    private String serviceName;
    private AbstractHAStrategy haStrategy;
    private AbstractLoadBalance<ServerInfo<NettyClient>> loadBalance;

    public Map<Method, Header> headerMapCache = Maps.newConcurrentMap();

    public ForestDynamicProxy(ServiceProviderConfig serviceProviderConfig, Class<?> interfaceClass,
                              AbstractServiceDiscovery discovery, FailoverCheckingStrategy failoverCheckingStrategy
            , GenericKeyedObjectPoolConfig keyedObjectPoolConfig) throws Exception {

        // 从注解中初始化配置
        initConfigFromAnnotation(interfaceClass);
        // 用传入的serviceProviderConfig覆盖注解的默认配置
        coverConfig(serviceProviderConfig);

        if (discovery == null) {
            discovery = AbstractServiceDiscovery.DEFAULT_DISCOVERY;
        }
        if (discovery instanceof LocalServiceDiscovery) {
            discovery.registerLocal(config.getServiceName(), ((LocalServiceDiscovery) discovery).getAddress());
        }

        switch (config.getHaStrategyType()) {
            case FAIL_FAST:
                haStrategy = new FailFastStrategy(keyedObjectPoolConfig);
                break;
            case FAIL_OVER:
                haStrategy = new FailoverStrategy(keyedObjectPoolConfig);
                break;
            default:
                haStrategy = new FailFastStrategy(keyedObjectPoolConfig);
                break;
        }
        switch (config.getLoadBalanceType()) {
            case RANDOM:
                loadBalance = new RandomLoadBalance<>(failoverCheckingStrategy, serviceName, discovery);
                break;
            default:
                loadBalance = new RandomLoadBalance<>(failoverCheckingStrategy, serviceName, discovery);
                break;
            // TODO: 2016/12/20
        }
        // 订阅事件，onRemove事件时，
        discovery.subscribe(serviceName, haStrategy);

    }

    private void coverConfig(ServiceProviderConfig serviceProviderConfig) {
        if (serviceProviderConfig == null) {
            return;
        }
        if (serviceProviderConfig.getLoadBalanceType() != null) {
            config.setLoadBalanceType(serviceProviderConfig.getLoadBalanceType());
        }
        if (serviceProviderConfig.getHaStrategyType() != null) {
            config.setHaStrategyType(serviceProviderConfig.getHaStrategyType());
        }
        if (serviceProviderConfig.getConnectionTimeout() > 0) {
            config.setConnectionTimeout(serviceProviderConfig.getConnectionTimeout());
        }

        // 覆盖method的配置
        for (Map.Entry<String, MethodConfig> methodConfigEntry : serviceProviderConfig.getMethodConfigMap().entrySet()) {
            MethodConfig methodConfigFromAnnotation = config.getMethodConfig(methodConfigEntry.getKey());
            if (methodConfigFromAnnotation == null) {
                LOGGER.warn("methodName is not exist. err methodName:{},serviceName:{}", methodConfigEntry.getKey(), serviceName);
                continue;
            }
            MethodConfig methodConfig = methodConfigEntry.getValue();
            methodConfig.setMethodName(methodConfigFromAnnotation.getMethodName());
            methodConfig.setServiceName(methodConfigFromAnnotation.getServiceName());
            config.registerMethodConfig(methodConfigEntry.getKey(), methodConfig);
        }
    }

    protected void initConfigFromAnnotation(Class<?> interfaceClass) {

        AnnotationProcessorsProvider processors = AnnotationProcessorsProvider.DEFAULT;
        registerAnnotationProcessors(processors);
        ServiceProvider serviceProvider = interfaceClass.getAnnotation(ServiceProvider.class);
        if (serviceProvider == null) {
            throw new ForestFrameworkException("interfaceClass " + interfaceClass + "ServiceProvider is null");
        }
        this.serviceName = Strings.isNullOrEmpty(serviceProvider.serviceName()) ? interfaceClass.getSimpleName() : serviceProvider.serviceName();
        // 加载注解配置作为默认配置
        config = ServiceProviderConfig.Builder.newBuilder()
                .withConnectionTimeout(serviceProvider.connectionTimeout())
                .withHaStrategyType(serviceProvider.haStrategyType())
                .withLoadBalanceType(serviceProvider.loadBalanceType())
                .build();
        config.setServiceName(serviceName);
        for (Method method : interfaceClass.getMethods()) {





            for (IAnnotationProcessor processor : processors.getProcessors()) {
                processor.process(serviceName, method, config);
            }
        }


    }

    public static void registerAnnotationProcessors(AnnotationProcessorsProvider processors) {
        processors.register(new HttpAnnotationProcessor());
        processors.register(new HystrixAnnotationProcessor());
        processors.register(new MethodProviderAnnotationProcessor());
    }

    public static <T> T newInstance(Class<T> clazz, ServiceProviderConfig serviceProviderConfig,
                                    AbstractServiceDiscovery registry, FailoverCheckingStrategy strategy
            , GenericKeyedObjectPoolConfig keyedObjectPoolConfig) throws Exception {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz},
                new ForestDynamicProxy(serviceProviderConfig, clazz, registry, strategy, keyedObjectPoolConfig));
    }

    public static <T> T newInstance(Class<T> clazz, ServiceProviderConfig serviceProviderConfig,
                                    AbstractServiceDiscovery registry, FailoverCheckingStrategy strategy) throws Exception {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz},
                new ForestDynamicProxy(serviceProviderConfig, clazz, registry, strategy, new GenericKeyedObjectPoolConfig()));
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodProvider methodProvider = method.getAnnotation(MethodProvider.class);
        if (methodProvider == null) {
            LOGGER.info("method:{} cannot find methodProvider.", method.getName());
            return null;
        }
        String methodName = Strings.isNullOrEmpty(methodProvider.methodName()) ? method.getName() : methodProvider.methodName();
        MethodConfig methodConfig = config.getMethodConfig(methodName);
        if (methodConfig == null) {
            LOGGER.info("serviceName:{},methodName is not found", serviceName, method.getName());
            return null;
        }
        Header header = headerMapCache.get(method);
        if (header == null) {
            header = Header.HeaderMaker.newMaker()
                    .loadWithMethodConfig(methodConfig).make();

        }
        Message message = new Message(header, new Request(serviceName, methodName, args));
        return haStrategy.call(message, loadBalance);

    }
}
