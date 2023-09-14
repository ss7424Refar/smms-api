package com.asv.http;

import lombok.Getter;

@Getter
public enum ResponseCode {

    SUCCESS(200,"操作成功"),
    FAILURE(201,"操作失败"),
    UNAUTHORIZED(301,"token验证失败"),
    TIMEOUT(401,"会话超时"),

    ERROR(500,"系统异常，请稍后重试");

    private final int code;

    private final String message;

    ResponseCode(int code, String message){
        this.code = code;
        this.message = message;
    }
}
