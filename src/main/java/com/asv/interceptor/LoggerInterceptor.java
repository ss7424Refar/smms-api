package com.asv.interceptor;

import com.asv.constant.Constant;
import com.asv.entity.User;
import com.asv.http.RepeatableHttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.util.Enumeration;

@Slf4j
public class LoggerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String userAgent = request.getHeader("device");
        User u = (User) request.getAttribute("login");
        String active = ObjectUtils.isEmpty(Constant.ACTIVE) ? "prod" : Constant.ACTIVE ;

        if (ObjectUtils.isEmpty(u)) {
            log.info("active: {}; 客户端: {}; 调用: {}; ", active, userAgent, request.getRequestURI());
        } else {
            log.info("active: {}; 用户: {}({}) ; 客户端: {}; 调用: {}; ", active, u.getUserName(), u.getUserNo(), userAgent, request.getRequestURI());
        }

        Enumeration<String> parameterNames = request.getParameterNames();
        StringBuilder parameters = new StringBuilder();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            parameters.append(paramName).append("=").append(paramValue).append(", ");
        }

        if (!parameters.toString().isEmpty()) {
            log.info("请求参数: " + parameters.substring(0, parameters.toString().length() - 2));
        }

        // 这里还是要重新调用一遍, 否则图片回显如果路径错误会出错.
        // 为了避免 getInputStream() has already been called for this request
        request = new RepeatableHttpServletRequest(request);
        BufferedReader reader = request.getReader();
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        if (!ObjectUtils.isEmpty(requestBody.toString())) {
            log.info("请求体数据: {} ", requestBody);
        }

        return true;
    }
}
