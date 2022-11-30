package com.maker.processsor;

import com.maker.config.MessageChannel;
import com.maker.vo.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;


@Component
public class MessageChannelPostProcessor implements BeanFactoryPostProcessor {
    private static final Logger LOGGER= LoggerFactory.getLogger(MessageChannelPostProcessor.class);
   /**
    * Spring初始化完成后，会调用此方法进行处理
    * */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {//由此beanFactory，可以通过getBean的方法，获取到需要的Bean对象实例

        MessageChannel channel=beanFactory.getBean(MessageChannel.class);
        LOGGER.info("初始化获得的MessageChannel对象：{}",channel);
        channel.setToken("Holy Shit!");
        channel.setHost("crazyFool.com");

    }
}
