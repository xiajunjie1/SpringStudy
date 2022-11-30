package com.maker.aop.advice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
@Aspect
@Configuration
public class ServiceAdviceAnnotation {
    private static final Logger LOGGER= LoggerFactory.getLogger(ServiceAdviceAnnotation.class);
    @Pointcut(value = "execution(public * com.maker..service..*.*(..)) and args(msg)")
    public void pointcut(){}
   @Before(value="execution(public * com.maker..service..*.*(..)) and args(msg)",argNames = "msg")
    public void beforeHandle(String msg){
        LOGGER.info("【{}】业务方法调用前置处理",msg);
    }
    @After(value = "execution(public * com.maker..service..*.*(..)) and args(msg)")
    public void afterHandle(String msg){
        LOGGER.info("【{}】业务方法调用后置处理",msg);
    }

    /**
     * 环绕通知
     * 通过ProceedingJoinPoint类，可以获取到调用的方法的所有信息
     *
     * */
    //@Around(value="pointcut()")
    public Object roundHandle(ProceedingJoinPoint point){
        LOGGER.debug("【环绕通知处理】目标对象：{}",point.getTarget());
        LOGGER.debug("【环绕通知处理】目标对象类型：{}",point.getKind());
        LOGGER.debug("【环绕通知处理】切面表达式：{}",point.getStaticPart());
        LOGGER.debug("【环绕通知处理】方法签名：{}",point.getSignature());
        LOGGER.debug("【环绕通知处理】源代码定位：{}",point.getSourceLocation());
        LOGGER.info("【通知前置处理】接收到的参数为：{}", Arrays.toString(point.getArgs()));
        Object returnValue=null;//接收目标方法的返回值
        try{
            returnValue=point.proceed(point.getArgs());//调用业务方法
        }catch (Throwable e){
            //后置异常处理
            LOGGER.error("【后置异常通知处理】业务方法产生异常，异常信息为：{}",e.getMessage());
        }
        LOGGER.info("【后置通知处理】业务方法执行完成，返回方法结果为：{}",returnValue);
        return returnValue;
    }

}
