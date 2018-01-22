package com.znq.rpc.client.cluster;

import com.znq.common.codec.Message;
import com.znq.rpc.client.cluster.lb.AbstractLoadBalance;

/**
 * Created by znq on 2016/12/7.
 */
public interface IHaStrategy<T> {

    Object call(Message message, AbstractLoadBalance<T> loadBalance) throws Exception;

    void clearPool(T key);
}
