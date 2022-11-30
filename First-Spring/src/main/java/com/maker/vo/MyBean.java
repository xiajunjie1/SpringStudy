package com.maker.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 此类在xml配置文件中进行注册
 * */
public class MyBean {
    private static final Logger LOGGER= LoggerFactory.getLogger(MyBean.class);

    public void init(){
        LOGGER.info("MyBean初始化");
    }
    public void onRecive(String msg){
        LOGGER.info("【消息接收】{}",msg);
    }
    public void destroy(){
        LOGGER.info("MyBean销毁");
    }

}
