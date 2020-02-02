package com.huanyu.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CustomEncoder extends MessageToByteEncoder<CustomProtocol> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          CustomProtocol customProtocol, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(customProtocol.getHead_data());
        byteBuf.writeByte(customProtocol.getIsRequest());
        byteBuf.writeLong(customProtocol.getRequestId());
        byteBuf.writeInt(customProtocol.getContentLength());
        byteBuf.writeBytes(customProtocol.getContent());
    }
}
