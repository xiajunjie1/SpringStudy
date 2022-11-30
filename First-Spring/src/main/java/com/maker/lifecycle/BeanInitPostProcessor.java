package com.maker.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class BeanInitPostProcessor implements BeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanInitPostProcessor.class);
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //每初始化一个Bean对象，都会调用该方法，在此处进行一个筛选，只有是BeanInit类初始化的时候才执行
        if("beanInit".equalsIgnoreCase(beanName)){
            LOGGER.info("【BeanPostProcessor-postProcessBeforeInitialization()】{}类初始化前执行。",bean.getClass().getName());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if("beanInit".equalsIgnoreCase(beanName)){
            LOGGER.info("【BeanPostProcessor-postProcessAfterInitialization()】{}类初始化后执行。",bean.getClass().getName());
        }
        return bean;
    }
}
