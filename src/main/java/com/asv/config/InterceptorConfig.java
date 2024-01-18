package com.asv.config;

import com.asv.interceptor.CorsInterceptor;
import com.asv.interceptor.LoggerInterceptor;
import com.asv.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Autowired
    Environment environment;

    @Bean
    public HandlerInterceptor getTokenInterceptor(){
        return new TokenInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有
        registry.addInterceptor(new CorsInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(getTokenInterceptor()).addPathPatterns("/**")
                // 这里需要添加 /error排除在外, 否则就会被拦截报301
                .excludePathPatterns(
                        "/login/login",
                        "/login/forgotPassword",
                        "/image/**",
                        "/win32/**",
                        "/device/download",
                        "/device/select", // 这里为了app端可以多开窗口取消token验证
                        "/error");//排除该登录地址或添加其他
        registry.addInterceptor(new LoggerInterceptor()).addPathPatterns("/**");

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置serve虚拟路径，addResourceHandler为前端访问的url目录，addResourceLocations为files相对应的本地路径
        // 如果有一个 upload/image/5_1678789396888.png请求，那程序会到本地的目录文件中找5_1678789396888.png文件
        registry.addResourceHandler("/image/**")
                .addResourceLocations("file:" + environment.getProperty("my-config.image-path"));
    }
}
