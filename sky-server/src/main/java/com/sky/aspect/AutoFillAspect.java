package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    // 定义切入点
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut(){}


    // 定义前置通知
    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行数据填充...");
        // 获取到当前被拦截方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); // 获取方法签名
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value(); //获取数据库操作类型

        // 准备赋值数据
        LocalDateTime localDateTime = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        // 得到要操作的实体对象
        Object entity = args[0];
        if (operationType == OperationType.INSERT) {
            // 为插入操作的实体对象赋值
            try {
                // 获取实体对象中的属性值
                Method setCreateTime = entity.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod("setCreateUser", Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);

                // 赋值
                setCreateTime.invoke(entity,localDateTime);
                setUpdateTime.invoke(entity,localDateTime);
                setCreateUser.invoke(entity,currentId);
                setUpdateUser.invoke(entity,currentId);
                log.info("为{}赋值完成",entity.getClass());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (operationType == OperationType.UPDATE){
            // 为更新操作的实体对象赋值
            try {
                // 获取实体对象中的属性值
                Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);

                // 赋值
                setUpdateTime.invoke(entity,localDateTime);
                setUpdateUser.invoke(entity,currentId);
                log.info("为{}赋值完成",entity.getClass());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
