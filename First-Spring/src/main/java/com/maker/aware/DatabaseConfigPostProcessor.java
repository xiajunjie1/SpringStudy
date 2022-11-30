package com.maker.aware;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 对每一个XxxxAware接口，都需要提供一个与之配套的BeanPostProcessor子类
 *  在此类中实现ApplicationContextAware是为了要实现ApplicationContext对象的注入，此接口由Spring提供，原理就是使用的Aware注入
 * */
//@Component
public class DatabaseConfigPostProcessor implements ApplicationContextAware, BeanPostProcessor {
    private ApplicationContext context;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context=applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Object config=this.context.getBean("databaseConfig");
        if(config==null){//Spring容器中未注册DatabaseConfig的Bean对象，无法进行注入，直接返回bean对象
            return bean;
        }
        if(config instanceof DatabaseConfig && bean instanceof IDatabaseConfigAware){
            //已经获取到DatabaseConfig对象，并且获取IDatabaseConfigAware子类（表示需要自动注入DatabaseConfig对象）
            ((IDatabaseConfigAware)bean).setDatabaseConfig((DatabaseConfig) config);//注入DatabaseConfig对象
        }
        return bean;
    }
}
