package com.maker.event;

import org.springframework.context.ApplicationEvent;
/**
 * 自定义事件
 * */
public class MyEvent extends ApplicationEvent {
    public MyEvent(Object source) {//事件源的传入
        super(source);
    }

}
