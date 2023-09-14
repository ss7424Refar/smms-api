package com.asv.interceptor;

import com.asv.dao.UserDao;
import com.asv.entity.User;
import com.asv.http.ResponseCode;
import com.asv.http.ResponseResult;
import com.asv.utils.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Autowired
    UserDao userDao;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                Claims claims = TokenUtil.parseToken(token);
                String userId = claims.getSubject();
                User user = userDao.findByUserId(Integer.valueOf(userId));

                if (user == null) {
                    throw new RuntimeException("拦截 - 无当前用户");
                }
                log.info("{}({}) token验证成功.", claims.get("userName"), userId);
                request.setAttribute("login", user);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof ExpiredJwtException) {
                    tokenCheckResult(response, ResponseCode.TIMEOUT);
                    return false;
                } else {
                    tokenCheckResult(response, ResponseCode.UNAUTHORIZED);
                    return false;
                }
            }
        }

        tokenCheckResult(response, ResponseCode.UNAUTHORIZED);
        return false;
    }

    private void tokenCheckResult(HttpServletResponse response, ResponseCode responseCode) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.write(new ObjectMapper().writeValueAsString(ResponseResult.custom(responseCode)));
        out.flush();
        out.close();
    }
}
