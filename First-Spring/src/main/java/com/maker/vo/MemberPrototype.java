package com.maker.vo;

import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class MemberPrototype {

    @Override
    public String toString() {
        return "【"+super.hashCode()+"】MessageSingleton对象";
    }
}
