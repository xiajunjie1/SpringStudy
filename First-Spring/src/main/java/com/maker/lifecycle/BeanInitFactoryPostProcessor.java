package com.maker.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
@Component
public class BeanInitFactoryPostProcessor implements BeanFactoryPostProcessor {
   private static final Logger LOGGER= LoggerFactory.getLogger(BeanInitFactoryPostProcessor.class);

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //此子类中可以获取到所有的Bean对象，它也是最先被执行的
        //并且BeanFactory拥有绝对的主导权，如果在此方法中获取了相应的Bean对象，那么便认为此对象已经
        //完成了初始化，即后面的初始化步骤不会在执行了
        LOGGER.info("【BeanFactoryPostProcessor-postProcessBeanFactory()】Bean对象初始化，Bean对象名称{}", Arrays.toString(beanFactory.getBeanDefinitionNames()));
    }
}
