package com.asv.aspect;

import com.asv.dao.DeviceDao;
import com.asv.entity.Device;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Aspect
@Component
@Slf4j
public class DeviceUpdateAspect {
    @Autowired
    DeviceDao deviceDao;

    @Pointcut("@annotation(com.asv.aspect.DeviceUpdateAnnotation)")
    public void logControllers() {}

    @AfterReturning(value = "logControllers()", returning = "result")
    public void logRequest(JoinPoint joinPoint, Object result) {
        HttpServletRequest httpServletRequest = null;
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof HttpServletRequest) {
                httpServletRequest = (HttpServletRequest) arg;
                break;
            }
        }
        if (httpServletRequest != null) {
            String deviceId = httpServletRequest.getParameter("deviceId");

            // 更新时间
            Device device = deviceDao.findByDeviceId(deviceId);
            if (device != null) {
                device.setUpdateDate(new Date());
                deviceDao.saveAndFlush(device);
                log.info("CLASS_METHOD [updatingDate] : {}/{} -  {}", joinPoint.getTarget().getClass().getName(),
                        joinPoint.getSignature().getName(), deviceId);
            }
        } else {
            log.warn("在AOP中找不到HttpServletRequest");
        }
        // 处理其他业务逻辑
    }
}
