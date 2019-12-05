package com.ldw.microservice.aop;

import org.aspectj.lang.annotation.*;

@Aspect
public class Audience {

    /**
     * 定义一个公共的切点
     */
    @Pointcut("execution(** com.spring.aop.service.Perfomance.perform(..))")
    public void performance() {
    }

    /**
     * 目标方法执行之前调用
     */
    @Before("performance()")
    public void silenceCellPhone() {
        System.out.println("Silencing cell phones");
    }

    /**
     * 目标方法执行之前调用
     */
    @Before("performance()")
    public void takeSeats() {
        System.out.println("Taking seats");
    }

    /**
     * 目标方法执行完后调用
     */
    @AfterReturning("performance()")
    public void applause() {
        System.out.println("CLAP CLAP CLAP");
    }

    /**
     * 目标方法发生异常时调用
     */
    @AfterThrowing("performance()")
    public void demandRefund() {
        System.out.println("Demanding a refund");
    }

}