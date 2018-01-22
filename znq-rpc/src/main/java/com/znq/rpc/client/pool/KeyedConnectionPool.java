package com.znq.rpc.client.pool;

import com.znq.common.ServerInfo;
import com.znq.rpc.client.Connection;
import com.znq.rpc.transport.NettyClient;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

/**
 * Created by znq on 2016/12/20.
 */
public class KeyedConnectionPool extends GenericKeyedObjectPool<ServerInfo<NettyClient>, Connection> {
    public KeyedConnectionPool(KeyedPooledObjectFactory<ServerInfo<NettyClient>, Connection> factory) {
        super(factory);
    }

    public KeyedConnectionPool(KeyedPooledObjectFactory<ServerInfo<NettyClient>, Connection> factory, GenericKeyedObjectPoolConfig config) {
        super(factory, config);
    }
}
