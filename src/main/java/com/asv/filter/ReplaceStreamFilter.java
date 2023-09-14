package com.asv.filter;

import com.asv.config.WhiteListConfig;
import com.asv.http.RepeatableHttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@WebFilter(urlPatterns = "/*")
@Slf4j
public class ReplaceStreamFilter implements Filter {

    @Autowired
    private WhiteListConfig whiteListConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 过滤方法
        HttpServletRequest req = (HttpServletRequest) request;
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        boolean doNormal = Boolean.FALSE;

        // 上传图片的时候, 如果重新包装请求了会找不到文件报错
        // Required request part 'file1' is not present
        for (String prefix : whiteListConfig.getWhiteList()){
            if ((contextPath + prefix).equals(url)) {
                log.warn("查找到白名单 " + url);
                doNormal = Boolean.TRUE;
                break;
            }

        }

        // 忽略特定的URL
        if (doNormal) {
            // 执行过滤操作
            chain.doFilter(request, response);
        } else {
            RepeatableHttpServletRequest bodyReaderWrapper = new RepeatableHttpServletRequest((HttpServletRequest) request);
            chain.doFilter(bodyReaderWrapper, response);
        }

    }

    @Override
    public void destroy() {

    }
}