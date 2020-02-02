package com.huanyu.rpc;

import com.alibaba.fastjson.JSON;
import io.netty.channel.*;

import java.lang.reflect.Method;

@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelDuplexHandler {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 用于获取客户端发来的数据信息
        CustomProtocol body = (CustomProtocol) msg;
        System.out.println("Server接受到客户端的信息 :" + body.toString());
        String content = new String(body.getContent());
        CustomProtocol.CustomRequest customRequest = JSON.parseObject(content, CustomProtocol.CustomRequest.class);
        ServiceConfig<?> serviceConfig = ServiceConfig.exportMap.get(customRequest.getInterfaceName());
        Object impl = serviceConfig.getServiceImpl();
        for (Method method : impl.getClass().getDeclaredMethods()) {
            if (method.getName().equals(customRequest.getMethodName())) {
                Object result = method.invoke(impl, customRequest.getArgs());
                // 会写数据给客户端
                CustomProtocol.CustomResponse customResponse = CustomProtocol.CustomResponse.builder()
                        .status(0).data(result).build();
                String str = JSON.toJSONString(customResponse);
                CustomProtocol response = CustomProtocol.builder()
                        .head_data(Constants.CUSTOM_PROTOCOL_HEAD_DATA)
                        .isRequest((byte)1)
                        .requestId(body.getRequestId())
                        .contentLength(str.getBytes().length)
                        .content(str.getBytes()).build();

                ctx.writeAndFlush(response);
            }
        }

        // 当有写操作时，不需要手动释放msg的引用
        // 当只有读操作时，才需要手动释放msg的引用
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
