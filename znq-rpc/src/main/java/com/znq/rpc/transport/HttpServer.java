package com.znq.rpc.transport;

import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.znq.common.config.ServerConfig;
import com.znq.rpc.IRouter;
import com.znq.rpc.support.jersey.NettyHandlerContainer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by znq on 2016/12/22.
 */
public class HttpServer extends AbstractServer {

    private static final URI BASE_URI = URI.create("http://localhost:8080/forest");

    public HttpServer(ServerConfig config) throws InterruptedException {
        this(null, config, config.httpPort);
    }

    public HttpServer(IRouter iRouter, ServerConfig config, int port) throws InterruptedException {
        super(iRouter, config, port);
    }

    @Override
    public ChannelInitializer<SocketChannel> newChannelInitializer(final IRouter iRouter) {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new HttpServerCodec());
                ch.pipeline().addLast(new HttpObjectAggregator(65536));
                ch.pipeline().addLast("chunkedHandler", new ChunkedWriteHandler());
                ch.pipeline().addLast("jerseyHandler", createJerseyHandler());
            }
        };
    }

    protected NettyHandlerContainer createJerseyHandler() {
        PackagesResourceConfig resourceConfig = new PackagesResourceConfig(config.basePackage);
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(NettyHandlerContainer.PROPERTY_BASE_URI, BASE_URI.toString());
        resourceConfig.setPropertiesAndFeatures(props);
        return ContainerFactory.createContainer(NettyHandlerContainer.class, resourceConfig);
    }
}
