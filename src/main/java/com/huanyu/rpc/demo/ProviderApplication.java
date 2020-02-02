package com.huanyu.rpc.demo;

import com.huanyu.rpc.ServiceConfig;

import java.util.concurrent.CountDownLatch;

public class ProviderApplication {
    public static final CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException {
        ServiceConfig<DemoService> serviceServiceConfig = ServiceConfig.<DemoService>builder()
                .interfaceClass(DemoService.class)
                .serviceImpl(new DemoServiceImpl()).build();
        serviceServiceConfig.export();
        countDownLatch.await();
    }

}
