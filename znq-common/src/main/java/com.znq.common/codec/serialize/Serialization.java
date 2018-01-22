package com.znq.common.codec.serialize;

import java.io.IOException;

/**
 * Created by znq on 2016/12/7.
 */
public interface Serialization {

    byte[] serialize(Object obj) throws IOException;

    <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException, ClassNotFoundException;
}

