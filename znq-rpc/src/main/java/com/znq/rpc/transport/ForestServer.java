package com.znq.rpc.transport;

import com.znq.common.config.ServerConfig;
import com.znq.rpc.IRouter;
import com.znq.rpc.codec.ForestDecoder;
import com.znq.rpc.codec.ForestEncoder;
import com.znq.rpc.handler.ForestProcessorHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by znq on 2016/12/9.
 */
public class ForestServer extends AbstractServer {

    public ForestServer(IRouter iRouter, ServerConfig config, int port) throws InterruptedException {
        super(iRouter, config, port);
    }

    public ChannelInitializer<SocketChannel> newChannelInitializer(final IRouter iRouter) {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("decoder", new ForestDecoder());
                ch.pipeline().addLast("encoder", new ForestEncoder());
                ch.pipeline().addLast("processor", new ForestProcessorHandler(iRouter));
            }
        };

    }
}
