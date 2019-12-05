package com.ldw.microservice.aop;

import com.ldw.microservice.entity.OperLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Aspect
public class LogAop {

    @Pointcut("execution(public * com.ldw.microservice.service.impl.BannerConfigServiceImpl.modifyCurrency(*))")
    private void logPointCut() {
    }


    @Before("logPointCut()")
    public void logBefore() {
        System.out.println("方法执行之前转载日志");
    }

    @AfterReturning(value = "logPointCut()", returning = "retObj")
    public void logAfterReturning(Object retObj) throws Throwable {
        System.out.println("方法执行返回后载入日志");
        OperLog operLog=(OperLog)retObj;
        System.out.println(operLog.getId()+"-----------------");
    }

    @After("logPointCut()")
    public void logAfter() {
        System.out.println("Finally载入日志");
    }

    @Around("logPointCut()")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("===around建议载入日志===" + new Date());
        Object o = pjp.proceed();
        return o;
    }

    @AfterThrowing("logPointCut()")
    public void logAfterThrowing() {
        System.out.println("===有参数异常载入日志===" + new Date());
    }
}