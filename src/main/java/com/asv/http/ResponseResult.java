package com.asv.http;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseResult {

    private int status;

    private Object data;

    private String message;

    private long timestamp;

    // 返回数据 在data中
    public static ResponseResult success(Object data) {
        return ResponseResult.builder()
                .timestamp(System.currentTimeMillis())
                .data(data)
                .message(ResponseCode.SUCCESS.getMessage())
                .status(ResponseCode.SUCCESS.getCode())
                .build();
    }

    public static ResponseResult success(Object data, String message) {
        return ResponseResult.builder()
                .timestamp(System.currentTimeMillis())
                .data(data)
                .message(message)
                .status(ResponseCode.SUCCESS.getCode())
                .build();
    }

    public static ResponseResult fail(String message) {
        return fail(null, message);
    }

    public static ResponseResult fail(Object data, String message) {
        return ResponseResult.builder()
                .timestamp(System.currentTimeMillis())
                .data(data)
                .message(message)
                .status(ResponseCode.FAILURE.getCode())
                .build();
    }

    public static ResponseResult custom(ResponseCode responseCode) {
        return ResponseResult.builder()
                .timestamp(System.currentTimeMillis())
                .data(null)
                .message(responseCode.getMessage())
                .status(responseCode.getCode())
                .build();
    }

}