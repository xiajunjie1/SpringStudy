package com.maker.config.profile.product;

import com.maker.di.config.MessageConfig_Profile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("product")
public class MessageConfigProduct {
    @Bean("messageConfigProfile")
    public MessageConfig_Profile getConfig(){
        MessageConfig_Profile config_profile=new MessageConfig_Profile();
        config_profile.setHostname("product.ojbk.cn");
        config_profile.setPort(800);
        config_profile.setEnable(true);
        return config_profile;
    }
}
