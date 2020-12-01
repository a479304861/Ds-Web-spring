package com.example.graduate_project.interceptor;


import com.example.graduate_project.dao.ResponseResult;
import com.example.graduate_project.utiles.ConstantUtils;
import com.example.graduate_project.utiles.RedisUtil;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Method;

@Component
public class ApiInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private Gson gson;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod= (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            CheckTooFrequentCommit methodAnnotation = handlerMethod.getMethodAnnotation(CheckTooFrequentCommit.class);
            if (methodAnnotation!=null){
                //判断提交是否太重复
                {
                    String remoteAddr = request.getRemoteAddr();
                    remoteAddr=remoteAddr.replace(':','-');
                        //TODO:
                    String hasCommit = (String) redisUtil.get(ConstantUtils.User.KEY_COMMIT+remoteAddr);
                    if (hasCommit!=null) {
                        response.setCharacterEncoding("UTF-8");
                        response.setContentType("application/json");
                        ResponseResult responseResult = ResponseResult.FAILED("提交过于频繁");
                        PrintWriter writer = response.getWriter();
                        writer.write(gson.toJson(responseResult));
                        writer.flush();
                        System.out.println("提交太频繁了");
                        return false;
                    }else{
                        redisUtil.set(ConstantUtils.User.KEY_COMMIT+remoteAddr,"true",30);
                    }
                }
            }
        }
        return true; //true放 false 拦截
    }
}
