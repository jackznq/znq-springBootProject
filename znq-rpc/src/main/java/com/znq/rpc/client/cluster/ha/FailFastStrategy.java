package com.znq.rpc.client.cluster.ha;

import com.znq.common.ServerInfo;
import com.znq.common.codec.Message;
import com.znq.rpc.client.cluster.lb.AbstractLoadBalance;
import com.znq.rpc.transport.NettyClient;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

/**
 * Created by znq on 2016/12/7.
 */
public class FailFastStrategy extends AbstractHAStrategy {

    public FailFastStrategy(GenericKeyedObjectPoolConfig config) {
        super(config);
    }

    @Override
    public Object call(Message message, AbstractLoadBalance<ServerInfo<NettyClient>> loadBalance) throws Exception {
        ServerInfo select = loadBalance.select(null);
        return remoteCall(select, message, loadBalance);
    }
}

