package com.maker.config.event;

import com.maker.event.listener.MyEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyEventConfig {
    @Bean
    public MyEventListener eventListener(){
        return new MyEventListener();
    }
}
