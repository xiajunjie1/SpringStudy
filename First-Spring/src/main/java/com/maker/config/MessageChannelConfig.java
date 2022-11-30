package com.maker.config;

import com.maker.config.MessageChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageChannelConfig {
    @Bean
    public MessageChannel messageChannel(){
        MessageChannel channel=new MessageChannel();
        channel.setHost("jayjxia");
        channel.setToken("okkkk!");
        return channel;
    }
}
