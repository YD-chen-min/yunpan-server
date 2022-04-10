package com.yandan.yunstorage.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Create by yandan
 * 2022/2/28  20:03
 */
@Aspect
@Component
public class ErrorLogAspect {
    @Autowired
    private Logger logger;

    @Pointcut("execution(public * com.yandan.yunstorage..*.*(..))")
    public void controllerLog(){}
    @AfterThrowing(pointcut = "controllerLog()",throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint,Exception e){
        logger.errorLogIn(e.getMessage(),e.getStackTrace());

    }
}
