package com.maker.consumer.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

public class RabbitMQMessageListener implements MessageListener{
   private static final Logger LOGGER= LoggerFactory.getLogger(RabbitMQMessageListener.class);

    @Override
    public void onMessage(Message message) {//用来处理接收到的消息
        LOGGER.info("接收到消息：{}",new String(message.getBody()));
    }
}
