package com.znq.rpc.client.cluster.ha;


import com.znq.common.Constants;
import com.znq.common.ServerInfo;
import com.znq.common.codec.Message;
import com.znq.common.codec.Response;
import com.znq.common.exception.ForestFrameworkException;
import com.znq.common.registry.AbstractServiceEventListener;
import com.znq.rpc.client.Connection;
import com.znq.rpc.client.cluster.IHaStrategy;
import com.znq.rpc.client.cluster.lb.AbstractLoadBalance;
import com.znq.rpc.client.pool.KeyedConnectionPool;
import com.znq.rpc.client.pool.KeyedConnectionPoolFactory;
import com.znq.rpc.transport.NettyClient;
import com.znq.rpc.transport.NettyResponseFuture;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


/**
 * Created by znq on 2016/12/20.
 */
public abstract class AbstractHAStrategy extends AbstractServiceEventListener implements IHaStrategy<ServerInfo<NettyClient>>
{

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractHAStrategy.class);

    private KeyedConnectionPool poolProvider;

    public AbstractHAStrategy(GenericKeyedObjectPoolConfig config) {
        poolProvider = new KeyedConnectionPool(new KeyedConnectionPoolFactory(), config);
    }

    protected Object remoteCall(ServerInfo<NettyClient> key, Message message, AbstractLoadBalance<ServerInfo<NettyClient>> loadBalance) {
        Object result;
        Connection connection = null;
        if (key == null) {
            throw new ForestFrameworkException("cannot get available key for server");
        }
        try {
            connection = poolProvider.borrowObject(key);
            NettyResponseFuture<Response> future = connection.request(message, Constants.DEFAULT_TIMEOUT);
            result = future.getPromise().await(Constants.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS).getResult();
            poolProvider.returnObject(key, connection);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            // 失败 上报数据，根据策略切换
            loadBalance.fail(key);
            if (connection != null) {
                try {
                    poolProvider.invalidateObject(key, connection);
                } catch (Exception e1) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            return null;
        }finally {
            key.activeCountIncrementAndGet();
        }
        return result;
    }

    @Override
    public void clearPool(ServerInfo<NettyClient> key) {
        poolProvider.clear(key);
    }


    @Override
    public void onRemove(ServiceInstance serviceInstance) {
        clearPool(new ServerInfo<NettyClient>(serviceInstance));
    }
}
