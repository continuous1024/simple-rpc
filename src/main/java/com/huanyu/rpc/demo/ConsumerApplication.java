package com.huanyu.rpc.demo;

import com.huanyu.rpc.ReferenceConfig;

public class ConsumerApplication {
    public static void main(String[] args) {
        ReferenceConfig<DemoService> referenceConfig = ReferenceConfig.<DemoService>builder()
                .host("127.0.0.1").port(2880).interfaceClass(DemoService.class).build();
        DemoService demoService = referenceConfig.refer();
        String result = demoService.hello("World");
        System.out.println(result);

        System.out.println(demoService.hello("Huan Yu"));
    }
}
