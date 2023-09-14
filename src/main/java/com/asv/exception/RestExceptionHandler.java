package com.asv.exception;

import com.asv.http.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice // RestController的增强类，可用于实现全局异常处理器
public class RestExceptionHandler {
    /**
     * valid验证, 美化之后错误信息提示更加友好
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult handle(MethodArgumentNotValidException e) {
        return ResponseResult.fail(e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseResult handle(ConstraintViolationException e) {
        ConstraintViolationException ex = (ConstraintViolationException) e;
        return ResponseResult.fail(ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(";")));
    }
    @ExceptionHandler(BindException.class)
    public ResponseResult handle(BindException e) {
        BindException ex = (BindException) e;
        return ResponseResult.fail(ex.getAllErrors().stream().map(ObjectError::getDefaultMessage).
                collect(Collectors.joining(";")));
    }

    /**
     * 默认全局异常处理。
     * @param e the e
     * @return ResponseResult
     */
    @ExceptionHandler(Exception.class) // 可以指定自定义异常或者其他
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 指定客户端收到的http状态码
    public ResponseResult exception(Exception e) {
        e.printStackTrace();
        log.error("全局异常信息 ex = {}", e.getMessage());
        return ResponseResult.fail(e.getMessage());
    }

}