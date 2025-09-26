package com.sky.aspect;


import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充处理逻辑
 */
@Component
@Aspect
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut() {}

    //前置通知，在通知中进行公共字段的赋值
    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint) {
        //可先进行调试，是否能进入该方法，提前在mapper方法添加AutoFill注解
        log.info("开始进行公共字段自动填充");
        //通过jointPoint获取方法签名对象
        MethodSignature signature=(MethodSignature) joinPoint.getSignature();
        //通过方法签名对象获取@AutoFill注解对象
        AutoFill autoFill=signature.getMethod().getAnnotation(AutoFill.class);
        //获取@AutoFill注解对象的值，便于后面对insert或update做不同的处理
        OperationType operationType=autoFill.value();

        //joinPoint获取原始方法的参数数组
        Object[] args = joinPoint.getArgs();
        if(args==null||args.length==0){
            return;
        }

        Object entity=args[0];

        LocalDateTime now=LocalDateTime.now();
        Long currentId= BaseContext.getCurrentId();
        //添加空判
//        if(currentId==null){
//            throw new IllegalAccessException("未设置当前用户ID，请检查登录拦截器或过滤器是否正确执行");
//        }
        //
        if(operationType== OperationType.INSERT){
            try{
                Method setCreateTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
                Method setCreateUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,Long.class);
                Method setUpdateTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setUpdateUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);

                setCreateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            }catch (Exception e){
                e.printStackTrace();
            }
        } else if (operationType==OperationType.UPDATE) {
            try{
                Method setUpdateTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setUpdateUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);

                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
