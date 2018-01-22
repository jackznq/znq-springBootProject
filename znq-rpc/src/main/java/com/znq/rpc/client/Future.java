package com.znq.rpc.client;

import java.util.concurrent.TimeUnit;

/**
 * Created by znq on 2016/12/7.
 */
public interface Future<T> {

    T await() throws Exception;

    T await(long amount, TimeUnit unit) throws Exception;
}
