package com.huanyu.rpc;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 自定义协议实践
 * 解决TCP粘包拆包
 * 1. 消息定长
 * 2. 包尾添加特殊分隔符
 * 3. 将消息分为消息头和消息体
 * 参考文章 https://blog.csdn.net/weixin_30716725/article/details/98138179
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomProtocol {

    // 协议的开始标志
    private int head_data;

    // 是请求还是响应
    private byte isRequest;

    private long requestId;

    // 传输长度
    private int contentLength;

    // 消息内容
    private byte[] content;

    @Override
    public String toString() {
        return "CustomProtocol{" +
                "head_data=" + head_data +
                ", isRequest=" + isRequest +
                ", requestId=" + requestId +
                ", contentLength=" + contentLength +
                ", content=" + new String(content) +
                '}';
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomRequest {
        private String interfaceName;
        private String methodName;
        private Object[] args;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomResponse {
        private int status;
        private Object data;
    }

    public static CustomProtocol newRequest(long requestId, String interfaceName, String methodName, Object[] args) {
        CustomProtocol.CustomRequest customRequest = CustomProtocol.CustomRequest.builder()
                .interfaceName(interfaceName).methodName(methodName).args(args).build();
        String data = JSON.toJSONString(customRequest);
        // 获得要发送信息的字节数组
        byte[] content = data.getBytes();
        // 要发送信息的长度
        int contentLength = content.length;
        return CustomProtocol.builder()
                .head_data(Constants.CUSTOM_PROTOCOL_HEAD_DATA)
                .isRequest((byte)0)
                .requestId(requestId)
                .contentLength(contentLength)
                .content(content).build();
    }
}
