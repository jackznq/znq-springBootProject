package com.znq.rpc.client.cluster.lb;

import com.znq.common.ServerInfo;
import com.znq.common.codec.Message;
import com.znq.common.registry.AbstractServiceDiscovery;
import com.znq.rpc.client.cluster.FailoverCheckingStrategy;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 低并发优先
 * Created by znq on 2016/12/22.
 */
public class ActiveWeightLoadBalance<T> extends AbstractLoadBalance<T> {

    public ActiveWeightLoadBalance(FailoverCheckingStrategy failoverCheckingStrategy, String serviceName, AbstractServiceDiscovery discovery) {
        super(failoverCheckingStrategy, serviceName, discovery);
    }

    @Override
    public ServerInfo<T> select(Message message) {
        List<ServerInfo<T>> availableServerList = getAvailableServerList();
        if (availableServerList.size() < 1) {
            return null;
        }
        Collections.sort(availableServerList, new Comparator<ServerInfo<T>>() {
            @Override
            public int compare(ServerInfo<T> o1, ServerInfo<T> o2) {
                return o1.activeCountGet() - o2.activeCountGet();
            }
        });
        return availableServerList.get(0);
    }
}
