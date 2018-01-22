package com.znq.rpc.client;

/**
 * Created by znq on 2016/12/7.
 */
public interface Callback<T> {

    void onReceive(T message);
}
