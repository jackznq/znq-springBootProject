package com.znq.rpc.client.cluster.lb;

import com.znq.common.registry.AbstractServiceDiscovery;
import com.znq.rpc.client.cluster.FailoverCheckingStrategy;
import com.znq.rpc.client.cluster.ILoadBalance;
import com.znq.rpc.client.cluster.ServerInfoList;

/**
 * Created by znq on 2016/12/7.
 */
public abstract class AbstractLoadBalance<T> extends ServerInfoList implements ILoadBalance<T>
{

    public AbstractLoadBalance(FailoverCheckingStrategy failoverCheckingStrategy, String serviceName, AbstractServiceDiscovery discovery) {
        super(failoverCheckingStrategy, serviceName, discovery);
    }

}
