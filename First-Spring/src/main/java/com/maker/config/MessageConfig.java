package com.maker.config;

import com.maker.vo.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class MessageConfig {
    @Bean
    @Profile("dev")
    public Message devMessage(){
        Message msg=new Message();
        msg.setPath("dev/jayjxia.com");
        return msg;
    }
    @Bean
    @Profile("test")
    public Message testMessage(){
        Message msg=new Message();
        msg.setPath("test/jayjxia.com");
        return msg;
    }
    @Bean
    @Profile("product")
    public Message productMessage(){
        Message msg=new Message();
        msg.setPath("product/jayjxia.com");
        return msg;
    }
}
