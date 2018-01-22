package com.znq.rpc.support.spring;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.znq.common.MetaInfo;
import com.znq.common.NetUtils;
import com.znq.common.annotation.ServiceExport;
import com.znq.common.config.ServerConfig;
import com.znq.common.config.ServiceExportConfig;
import com.znq.common.registry.AbstractServiceDiscovery;
import com.znq.rpc.ForestRouter;
import com.znq.rpc.client.proxy.processor.AnnotationProcessorsProvider;
import com.znq.rpc.client.proxy.processor.IAnnotationProcessor;
import com.znq.rpc.client.proxy.processor.ServiceExportProcessor;
import com.znq.rpc.transport.ForestServer;
import com.znq.rpc.transport.ForestServerFactory;
import com.znq.rpc.transport.HttpServer;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by znq on 2016/12/9.
 */
public class ForestServerBean {

    private final static Logger LOGGER = LoggerFactory.getLogger(ForestServerBean.class);

    AnnotationProcessorsProvider processors = AnnotationProcessorsProvider.DEFAULT;

    private ApplicationContext context;

    private ForestServerFactory factory;

    private AbstractServiceDiscovery registry;

    private Set<String> serviceProviderKey = Sets.newHashSet();

    private boolean startHttpServer;


    public void start() throws InterruptedException {

        ServerConfig serverConfig = context.getBean(ServerConfig.class);
        factory = new ForestServerFactory(serverConfig);
        register();
        init();

        if (startHttpServer) {
            startHttp();
        }
    }

    public void init() {
        for (String beanName : context.getBeanNamesForAnnotation(ServiceExport.class)) {
            Object bean = context.getBean(beanName);
            // init config
            ServiceExportConfig config = new ServiceExportConfig();
            for (Method method : bean.getClass().getMethods()) {
                for (IAnnotationProcessor processor : processors.getProcessors()) {
                    processor.process(bean.getClass(), method, config);
                }
            }
            // init router
            ForestRouter router = new ForestRouter(context);
            router.init(bean, config);
            String serviceName = config.getServiceName();
            int port = config.getPort();
            // 同一个端口只注册一个服务
            String serviceKey = serviceName + "|" + port;
            if (Strings.isNullOrEmpty(serviceName) || port == 0 || serviceProviderKey.contains(serviceKey)) {
                continue;
            }
            serviceProviderKey.add(serviceKey);
            try {
                ForestServer server = factory.createServer(router, config);
                server.start();
                // 注册服务
                ServiceInstance<MetaInfo> serviceInstance = ServiceInstance.<MetaInfo>builder()
                        .name(serviceName)
                        .address(NetUtils.getLocalAddress().getHostAddress())
                        .port(port)
                        .build();
                if (registry == null) {
                    registry = AbstractServiceDiscovery.DEFAULT_DISCOVERY;
                }
                registry.registerService(serviceInstance);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

        }

    }

    public void register() {
        processors.register(new ServiceExportProcessor());
    }


    public AbstractServiceDiscovery getRegistry() {
        return registry;
    }

    public void setRegistry(AbstractServiceDiscovery registry) {
        this.registry = registry;
    }

    public ApplicationContext getContext() {
        return context;
    }

    public void setContext(ClassPathXmlApplicationContext context) {
        this.context = context;
    }

    public void startHttp() throws InterruptedException {
        new HttpServer(context.getBean(ServerConfig.class)).start();
    }


    public boolean isStartHttpServer() {
        return startHttpServer;
    }

    public void setStartHttpServer(boolean startHttpServer) {
        this.startHttpServer = startHttpServer;
    }
}
