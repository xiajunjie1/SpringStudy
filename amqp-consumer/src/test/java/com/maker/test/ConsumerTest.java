package com.maker.test;

import com.maker.consumer.StartConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;

@ContextConfiguration(classes = StartConsumer.class)
@ExtendWith(SpringExtension.class)
public class ConsumerTest {
    private static final Logger LOGGER= LoggerFactory.getLogger(ConsumerTest.class);

    @Test
    public void springConsumerTest() throws Exception{//监听测试，什么都不操作，如果有消息就能监听到
        LOGGER.info("消费者开始执行");
        TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
    }
}
