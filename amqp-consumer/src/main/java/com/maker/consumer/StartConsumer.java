package com.maker.consumer;

import org.springframework.context.annotation.ComponentScan;
/**
 * 创建Consumer
 *  1.在profiles/dev下创建config/amqp.properties配置文件
 *  2.创建消息监听器（此监听器用来监听消息的接收，如何处理接收到的消息都在监听器中定义）
 *  3.创建RabbitMQConfig配置类，用来配置ConnectionFactory，channel，exchange、队列，虚拟主机等
 *  4.编写测试类，进行测试
 *
 *  使用注解的方式配置Consumer
 *      使用注解需要修改监听器类，并且在配置类中ListenerContainer也需要修改
 *      使用注解方式的好处在于：不需要强制实现接口，消息接收的处理类，接收的类型可以自行定义
 *      也就是说可以接收各种对象了。
 *
 * 消息的批量处理
 *      如果生产者发送了多条信息，按照原本的配置方法，消费者只能一条一条的处理
 *      也就是每处理一条信息，都会占用IO资源，如果能将其换成批量处理的话，那么可以
 *      减少IO资源的消耗
 *      修改监听器消息处理类，参数变为集合List
 *      修改Config类中的RabbitListenerContainerFactory注册方法
 * */
@ComponentScan("com.maker.consumer")
public class StartConsumer {
}
