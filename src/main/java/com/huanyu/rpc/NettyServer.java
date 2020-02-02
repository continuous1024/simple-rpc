package com.huanyu.rpc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyServer {

    public void bind(String host, int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.SO_BACKLOG, 1024) // 设置tcp缓冲区 // (5)
                .childOption(ChannelOption.SO_KEEPALIVE, true) // (6)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        // 添加自定义协议的编解码工具
                        ch.pipeline().addLast(new CustomDecoder());
                        ch.pipeline().addLast(new CustomEncoder());
                        // 处理网络IO
                        ch.pipeline().addLast(new NettyServerHandler());
                    }
                });

        // bind
        ChannelFuture channelFuture = b.bind(host, port);
        channelFuture.syncUninterruptibly();
    }
}
