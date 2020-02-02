package com.huanyu.rpc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceConfig<T> {
    public static final Map<String, ServiceConfig<?>> exportMap
            = new ConcurrentHashMap<>();

    private static final int DEFAULT_PORT = 2880;

    /**
     * 接口类
     */
    private Class<T> interfaceClass;

    /**
     * 接口实现类实例
     */
    private T serviceImpl;

    /**
     * 服务导出
     */
    public void export() {
        // 保存服务实现
        exportMap.put(interfaceClass.getName(), this);

        // 启动netty server
        String host = NetUtils.getLocalAddress();
        new NettyServer().bind(host, DEFAULT_PORT);

        System.out.println(interfaceClass.getName() + "服务暴露成功，地址: " + host + ":" + DEFAULT_PORT + ", 等待调用！");
    }
}
