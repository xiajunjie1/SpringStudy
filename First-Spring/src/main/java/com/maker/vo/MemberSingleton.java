package com.maker.vo;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class MemberSingleton {

    @Override
    public String toString() {
        return "【"+super.hashCode()+"】MessageSingleton对象";
    }

}
