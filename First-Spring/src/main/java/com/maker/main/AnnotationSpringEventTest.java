package com.maker.main;

import com.maker.config.event.MyEventConfig;
import com.maker.event.MyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AnnotationSpringEventTest {
    private static final Logger LOGGER= LoggerFactory.getLogger(AnnotationSpringEventTest.class);

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext();
        context.register(MyEventConfig.class);
        context.refresh();
        MyEvent event=new MyEvent("my name is jayjxia");
        //启动Spring后，进行事件的发布，事件监听器进行事件的接收处理
        context.publishEvent(event);//事件发布

    }
}
