package com.maker.config.profile.test;

import com.maker.di.config.MessageConfig_Profile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class MessageConfigTest {
    @Bean("messageConfigProfile")
    public MessageConfig_Profile getConfig(){
        MessageConfig_Profile config_profile=new MessageConfig_Profile();
        config_profile.setHostname("test.ojbk.cn");
        config_profile.setPort(800);
        config_profile.setEnable(true);
        return config_profile;
    }
}
