package com.maker.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component("MyBean2")
public class MyBean2 implements InitializingBean, DisposableBean {
    private static final Logger LOGGER= LoggerFactory.getLogger(MyBean2.class);

    public void onRecive(String msg){
        LOGGER.info("【收到消息】{}",msg);
    }

    @Override
    public void destroy() throws Exception {//Bean对象销毁时的方法
        LOGGER.info("MyBean对象销毁");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("MyBean对象初始化");
    }
}
