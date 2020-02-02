package com.huanyu.rpc.demo;

public class DemoServiceImpl implements DemoService {
    @Override
    public String hello(String name) {
        return "Hello, " + name + "!";
    }
}
