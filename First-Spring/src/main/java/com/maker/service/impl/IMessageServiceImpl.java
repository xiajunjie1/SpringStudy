package com.maker.service.impl;

import com.maker.service.IMessageService;

public class IMessageServiceImpl implements IMessageService {

    @Override
    public String echo(String msg) {
        return "【Echo】"+msg;
    }
}
