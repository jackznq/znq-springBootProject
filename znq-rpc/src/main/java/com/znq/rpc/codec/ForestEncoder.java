package com.znq.rpc.codec;


import com.znq.common.CompressType;
import com.znq.common.EventType;
import com.znq.common.SerializeType;
import com.znq.common.codec.Header;
import com.znq.common.codec.Message;
import com.znq.common.codec.compress.Compress;
import com.znq.common.codec.serialize.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by znq on 2016/12/7.
 */
public class ForestEncoder extends MessageToByteEncoder<Message>
{

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        Header header = message.getHeader();
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getVersion());
        byteBuf.writeByte(header.getExtend());
        byteBuf.writeLong(header.getMessageID());
        Object content = message.getContent();
        // 心跳消息,没有body
        if (content == null && EventType.typeofHeartBeat(header.getExtend())) {
            byteBuf.writeInt(0);
            return;
        }
        Serialization serialization = SerializeType.getSerializationByExtend(header.getExtend());
        Compress compress = CompressType.getCompressTypeByValueByExtend(header.getExtend());
        byte[] payload = compress.compress(serialization.serialize(content));
        byteBuf.writeInt(payload.length);
        byteBuf.writeBytes(payload);
    }
}
