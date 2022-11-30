package com.maker.producer;

import org.springframework.context.annotation.ComponentScan;
/**
 * 创建Producer
 *  1.在profiles/dev下创建config/amqp.properties配置文件
 *  2.创建RabbitMQConfig配置类，用来配置ConnectionFactory，exchange等
 *  3.创建IMessageService及其实现类
 *  4.编写测试类，进行测试
 *
 *  配置批量消息发送
 *      需要在配置类中注册一个TaskScheduler Bean对象
 *
 * */
@ComponentScan("com.maker.producer")
public class StartProducer {
}
