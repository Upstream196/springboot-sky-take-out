package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler) throws Exception{
        //添加拦截日志
        log.info("JwtTokenAdminInterceptor.preHandle called,handler={}",handler);
        //判断当前拦截到的是Controller的方法还是其他资源
        if(!(handler instanceof HandlerMethod)){
            //当前拦截到的不是动态方法，直接放行
            return true;
        }
        try{
            String token=request.getHeader(jwtProperties.getAdminTokeName());

            Claims claims= JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(),token);

            Long empId=Long.valueOf((String) claims.get(JwtClaimsConstant.EMP_ID));
            log.info("登录的员工id：{}",empId);
            BaseContext.setCurrentId(empId);
            return true;
        }catch (Exception e){
            response.setStatus(401);
            return false;
        }
    }

}
