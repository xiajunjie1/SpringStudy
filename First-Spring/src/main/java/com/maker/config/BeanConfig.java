package com.maker.config;

import com.maker.di.config.MessageConfig;
import com.maker.di.config.MessageConfig_2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class BeanConfig {
    /**
     *  注册MessageConfig Bean对象，Bean名称为messageConfigMain
     *
     * */
    @Bean
    public MessageConfig_2 messageConfigMain(){
        MessageConfig_2 config=new MessageConfig_2();
        config.setHostname("abc.com");
        config.setPort(8080);
        config.setEnable(true);
        return config;

    }

    @Bean
    public MessageConfig_2 messageConfigBak(){
        MessageConfig_2 config=new MessageConfig_2();
        config.setHostname("abcd.com");
        config.setPort(8181);
        config.setEnable(true);
        return config;

    }
}
