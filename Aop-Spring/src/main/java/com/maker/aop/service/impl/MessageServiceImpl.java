package com.maker.aop.service.impl;

import com.maker.aop.service.IMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements IMessageService {

    private static final Logger LOGGER= LoggerFactory.getLogger(MessageServiceImpl.class);
    @Override
    public void echo(String msg) {
        LOGGER.info("【消息响应】{}",msg);
    }
}
