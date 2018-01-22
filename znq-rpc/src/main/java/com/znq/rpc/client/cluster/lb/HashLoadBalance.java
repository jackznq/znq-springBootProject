package com.znq.rpc.client.cluster.lb;

import com.znq.common.ServerInfo;
import com.znq.common.codec.Message;
import com.znq.common.codec.Request;
import com.znq.common.registry.AbstractServiceDiscovery;
import com.znq.rpc.client.cluster.FailoverCheckingStrategy;

import java.util.Arrays;
import java.util.List;

/**
 * Created by znq on 2016/12/22.
 */
public class HashLoadBalance<T> extends AbstractLoadBalance<T> {

    public HashLoadBalance(FailoverCheckingStrategy failoverCheckingStrategy, String serviceName, AbstractServiceDiscovery discovery) {
        super(failoverCheckingStrategy, serviceName, discovery);
    }

    @Override
    public ServerInfo<T> select(Message message) {
        List<ServerInfo<T>> availableServerList = getAvailableServerList();
        return availableServerList.get(getHash((Request) message.getContent()) % availableServerList.size());
    }

    private int getHash(Request request) {
        int hashcode;
        if (request.getArgs() == null || request.getArgs().length == 0) {
            hashcode = request.hashCode();
        } else {
            hashcode = Arrays.hashCode(request.getArgs());
        }
        return 0x7fffffff & hashcode;
    }
}
