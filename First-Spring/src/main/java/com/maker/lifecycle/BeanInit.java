package com.maker.lifecycle;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
/**
 * 观察BeanInit对象初始化的过程
 *  这里让其实现了两个初始化相关的操作接口
 *  一个是在初始化前调用的操作，一个是在初始化后调用的操作（且必须为singleton模式，否则不会被调用）
 * */
@Component
public class BeanInit implements InitializingBean, SmartInitializingSingleton {
    private static final Logger LOGGER= LoggerFactory.getLogger(BeanInit.class);
    public BeanInit(){
        LOGGER.info("【BeanInit对象】构造方法调用！");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("【InitializingBean-afterPropertiesSet()】进行BeanInit对象初始化操作");
    }

    @Override
    public void afterSingletonsInstantiated() {
        LOGGER.info("【SmartInitializingSingleton-afterSingletonsInstantiated()】BeanInit对象初始化完成，并且该对象为单例");
    }
}
