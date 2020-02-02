# simple-rpc
算是最简单的RPC
- 使用接口进行RPC调用
- 使用Netty进行通信
- 使用JDK动态代理来实现客户端代理
- 使用CompletableFuture获取请求结果

## 示例
### 服务接口
```java
public interface DemoService {
    String hello(String name);
}

```
### 暴露服务
```java
ServiceConfig<DemoService> serviceServiceConfig = ServiceConfig.<DemoService>builder()
        .interfaceClass(DemoService.class)
        .serviceImpl(new DemoServiceImpl()).build();
serviceServiceConfig.export();
```

### 服务调用
```java
ReferenceConfig<DemoService> referenceConfig = ReferenceConfig.<DemoService>builder()
        .host("127.0.0.1").port(2880).interfaceClass(DemoService.class).build();
DemoService demoService = referenceConfig.refer();
String result = demoService.hello("World");
System.out.println(result);

System.out.println(demoService.hello("Huan Yu"));
```

## 借鉴Dubbo源码实现
阅读Dubbo源码之前或者之后，查看该源码会对rpc有一个很清晰的认识。