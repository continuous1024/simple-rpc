package com.huanyu.rpc;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceConfig<T> implements InvocationHandler {
    public static final AtomicLong REQUEST_ID = new AtomicLong(0);
    private static final Map<String, Channel> channelMap = new ConcurrentHashMap<>();
    public static final Map<Long, CompletableFuture<Object>> futureMap = new ConcurrentHashMap<>();
    /**
     * 接口类
     */
    private Class<T> interfaceClass;

    /**
     * 服务提供者host
     * TODO 服务自动发现
     */
    private String host;

    /**
     * 服务提供者port
     * TODO 服务自动发现
     */
    private int port;

    /**
     * 服务引用
     * @return
     */
    public T refer() {
        try {
            Channel channel = new NettyClient().connect(host, port);
            if (channel != null) {
                channelMap.put(interfaceClass.getName(), channel);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Set<Class<?>> interfaces = new HashSet<>();
        interfaces.add(interfaceClass);
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                interfaces.toArray(new Class<?>[0]), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String interfaceName = method.getDeclaringClass().getName();
        Channel channel = channelMap.get(interfaceName);
        if (channel != null) {
            long requestId = newId();
            CustomProtocol request = CustomProtocol.newRequest(requestId, interfaceName, method.getName(), args);
            CompletableFuture<Object> completableFuture = new CompletableFuture<>();
            futureMap.put(requestId, completableFuture);
            channel.writeAndFlush(request);
            return completableFuture.get();
        }

        return null;
    }

    private static long newId() {
        // getAndIncrement() When it grows to MAX_VALUE, it will grow to MIN_VALUE, and the negative can be used as ID
        return REQUEST_ID.getAndIncrement();
    }
}
