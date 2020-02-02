package com.huanyu.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class NettyClient {
    private int connectTimeout = 300000;

    public Channel connect(String host, int port) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.TCP_NODELAY, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                // 添加自定义协议的编解码工具
                ch.pipeline().addLast(new CustomDecoder());
                ch.pipeline().addLast(new CustomEncoder());
                // 处理网络IO
                ch.pipeline().addLast(new NettyClientHandler());
            }
        });

        // Start the client.
        ChannelFuture f = b.connect(host, port);
        boolean ret = f.awaitUninterruptibly(connectTimeout, MILLISECONDS);
        if (ret && f.isSuccess()) {
            return f.channel();
        }
        return null;
    }
}
