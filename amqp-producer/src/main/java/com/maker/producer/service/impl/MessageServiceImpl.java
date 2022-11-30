package com.maker.producer.service.impl;

import com.maker.common.Dept;
import com.maker.producer.service.IMessageService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:config/amqp.properties")
public class MessageServiceImpl implements IMessageService {
    @Value("${amqp.rabbitmq.routing.key}")
    private String routingKey;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Override
    public void send(String msg) throws Exception {
        amqpTemplate.convertAndSend(this.routingKey,msg);
    }
    @Override
    public void sendDept(Dept dept) throws Exception{
        amqpTemplate.convertAndSend(this.routingKey,dept);
    }
}
