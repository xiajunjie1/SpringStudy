package com.maker.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.Lifecycle;
import org.springframework.stereotype.Component;

@Component("MyBean4")
public class MyBean4 implements Lifecycle {
    private static final Logger LOGGER= LoggerFactory.getLogger(MyBean4.class);
    private boolean isrunning=false;
    @Override
    public void start() {
        LOGGER.info("MyBean初始化");
        this.isrunning=true;//当前已经启动
        //如果isRunning返回的是true，那么便不会调用start方法
    }

    public void onReceive(String msg){
        LOGGER.info("【收到消息】{}",msg);
    }

    @Override
    public void stop() {
        LOGGER.info("MyBean销毁");
        this.isrunning=false;
    }

    @Override
    public boolean isRunning() {
        return this.isrunning;
    }
}
