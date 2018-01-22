package com.znq.rpc.client.cluster;


import com.znq.common.ServerInfo;
import com.znq.common.codec.Message;

/**
 * Created by znq on 2016/12/6.
 */
public interface ILoadBalance<T> {

    ServerInfo<T> select(Message message);
}
