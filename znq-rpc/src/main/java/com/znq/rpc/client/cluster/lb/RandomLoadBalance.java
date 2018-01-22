package com.znq.rpc.client.cluster.lb;

import com.znq.common.ServerInfo;
import com.znq.common.codec.Message;
import com.znq.common.registry.AbstractServiceDiscovery;
import com.znq.rpc.client.cluster.FailoverCheckingStrategy;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by znq on 2016/12/7.
 */
public class RandomLoadBalance<T> extends AbstractLoadBalance<T> {

    public RandomLoadBalance(FailoverCheckingStrategy failoverCheckingStrategy, String serviceName, AbstractServiceDiscovery discovery) {
        super(failoverCheckingStrategy, serviceName, discovery);
    }

    @Override
    public ServerInfo<T> select(Message message) {
        List<ServerInfo<T>> availableServerList = getAvailableServerList();
        int idx = (int) (ThreadLocalRandom.current().nextDouble() * availableServerList.size());
        return availableServerList.get((idx) % availableServerList.size());
    }
}
