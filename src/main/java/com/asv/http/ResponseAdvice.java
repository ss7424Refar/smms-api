package com.asv.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Collection;
import java.util.Map;

@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }
    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if(o instanceof String){
            // 这里如果不设置会返回text/html, 在拦截器里面写不生效, 很神奇.
            serverHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return new ObjectMapper().writeValueAsString(ResponseResult.success(o));
        }
        if (o instanceof ResponseResult) {
            return o;
        }
        if (o instanceof Collection || o instanceof Map) {
            return ResponseResult.success(o);
        }
        return ResponseResult.fail(o, "未知错误 ~");
    }
}