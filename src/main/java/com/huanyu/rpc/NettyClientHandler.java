package com.huanyu.rpc;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.CompletableFuture;

@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelDuplexHandler {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            // 用于获取服户端发来的数据信息
            CustomProtocol body = (CustomProtocol) msg;
            System.out.println("Client接受的服户端的信息 :" + body.toString());
            String content = new String(body.getContent());
            CustomProtocol.CustomResponse customResponse = JSON.parseObject(content, CustomProtocol.CustomResponse.class);
            CompletableFuture<Object> completableFuture = ReferenceConfig.futureMap.get(body.getRequestId());
            completableFuture.complete(customResponse.getData());
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
