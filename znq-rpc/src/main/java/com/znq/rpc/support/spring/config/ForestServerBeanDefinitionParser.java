package com.znq.rpc.support.spring.config;

import com.google.common.base.Strings;
import com.znq.common.config.ServerConfig;
import com.znq.common.util.ForestUtil;
import com.znq.rpc.support.spring.ForestServerBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Element;

/**
 * Created by znq on 2017/3/14.
 */
public class ForestServerBeanDefinitionParser implements BeanDefinitionParser
{

    private final static String START_HTTP_SERVER = "startHttpServer";



    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        boolean startHttpSrv = false;
        String id = ForestUtil.getBeanId(element, ForestServerBean.class);
        String startHttpServer = element.getAttribute(START_HTTP_SERVER);
        if (!Strings.isNullOrEmpty(startHttpServer)) {
            startHttpSrv = Boolean.parseBoolean(startHttpServer);
        }
        ApplicationContext context =((ApplicationContext) parserContext.getReaderContext().getReader().getResourceLoader());
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ForestServerBean.class);
        builder.addPropertyValue(START_HTTP_SERVER, startHttpSrv);
        builder.addPropertyValue("context", context);
        builder.setInitMethodName("start");

        parserContext.getRegistry().registerBeanDefinition(ForestUtil.getDefaultBeanName(ServerConfig.class),
                BeanDefinitionBuilder.genericBeanDefinition(ServerConfig.class).getBeanDefinition());


        parserContext.getRegistry().registerBeanDefinition(id, builder.getBeanDefinition());


        return null;
    }


}
