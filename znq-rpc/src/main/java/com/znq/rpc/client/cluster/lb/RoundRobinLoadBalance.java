package com.znq.rpc.client.cluster.lb;

import com.znq.common.ServerInfo;
import com.znq.common.codec.Message;
import com.znq.common.registry.AbstractServiceDiscovery;
import com.znq.rpc.client.cluster.FailoverCheckingStrategy;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by znq on 2016/12/22.
 */
public class RoundRobinLoadBalance<T> extends AbstractLoadBalance<T> {

    private AtomicInteger idx = new AtomicInteger(0);

    public RoundRobinLoadBalance(FailoverCheckingStrategy failoverCheckingStrategy, String serviceName, AbstractServiceDiscovery discovery) {
        super(failoverCheckingStrategy, serviceName, discovery);
    }

    @Override
    public ServerInfo<T> select(Message message) {
        List<ServerInfo<T>> availableServerList = getAvailableServerList();
        return availableServerList.get(idx.incrementAndGet() % availableServerList.size());
    }
}
