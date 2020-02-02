package com.huanyu.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class CustomDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf,
                          List<Object> list) throws Exception {
        // 可读长度必须大于8(开始标志和传输长度为int类型，int类型占4个字节)
        if (byteBuf.readableBytes() >= Constants.CUSTOM_PROTOCOL_BASE_LENGTH) {
            // 要传输的数据,长度不应该超过2048，防止socket流的攻击
            if (byteBuf.readableBytes() > 2048) {
                byteBuf.skipBytes(byteBuf.readableBytes());
            }
            // 记录包头开始的index
            int beginReader;

            while (true) {
                // 获取包头开始的index
                beginReader = byteBuf.readerIndex();
                // 标记包头开始的index
                byteBuf.markReaderIndex();
                // 读到了协议的开始标志，结束while循环
                if (byteBuf.readInt() == Constants.CUSTOM_PROTOCOL_HEAD_DATA) {
                    break;
                }

                // 未读到包头，略过一个字节
                // 每次略过，一个字节，去读取，包头信息的开始标记
                byteBuf.resetReaderIndex();
                byteBuf.readByte();

                // 当略过，一个字节之后，
                // 数据包的长度，又变得不满足
                // 此时，应该结束。等待后面的数据到达
                if (byteBuf.readableBytes() < Constants.CUSTOM_PROTOCOL_BASE_LENGTH) {
                    return;
                }
            }

            // 消息的类型
            byte isRequest = byteBuf.readByte();
            // 消息ID
            long requestId = byteBuf.readLong();
            // 消息的长度
            int length = byteBuf.readInt();
            // 判断请求数据包数据是否到齐
            if (byteBuf.readableBytes() < length) {
                // 还原读指针
                byteBuf.readerIndex(beginReader);
                return;
            }

            // 读取data数据
            byte[] data = new byte[length];
            byteBuf.readBytes(data);

            CustomProtocol protocol = CustomProtocol.builder()
                    .head_data(Constants.CUSTOM_PROTOCOL_HEAD_DATA)
                    .isRequest(isRequest)
                    .requestId(requestId)
                    .contentLength(length)
                    .content(data).build();
            list.add(protocol);
        }
    }
}
