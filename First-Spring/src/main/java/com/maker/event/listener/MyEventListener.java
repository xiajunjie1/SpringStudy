package com.maker.event.listener;

import com.maker.event.MyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class MyEventListener implements ApplicationListener<MyEvent> {
    private static final Logger LOGGER= LoggerFactory.getLogger(MyEventListener.class);
    @Override
    public void onApplicationEvent(MyEvent event) {
        LOGGER.info("事件源：{}",event.getSource());
    }
}
