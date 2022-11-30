package com.maker.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
@Component("MyBean3")
public class MyBean3 {
    private static final Logger LOGGER= LoggerFactory.getLogger(MyBean3.class);
   @PostConstruct//JSR-250提供的注解，标志初始化方法
    public void init(){
        LOGGER.info("MyBean初始化！");
   }
    public void onRecive(String msg){
        LOGGER.info("【接收到消息】{}",msg);
    }
    @PreDestroy//JSR-250提供的注解，标志销毁方法
    public void destroy(){
       LOGGER.info("MyBean销毁！");
    }
}
