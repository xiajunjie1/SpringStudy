package com.maker.producer.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.batch.BatchingStrategy;
import org.springframework.amqp.rabbit.batch.SimpleBatchingStrategy;
import org.springframework.amqp.rabbit.connection.PooledChannelConnectionFactory;
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 配置类
 *  消费端配置
 * */
@Configuration
@PropertySource("classpath:config/amqp.properties")
public class RabbitMQConfig {
    @Value("${amqp.rabbitmq.host}")
    private String host;
    @Value("${amqp.rabbitmq.port}")
    private Integer port;
    @Value("${amqp.rabbitmq.username}")
    private String username;
    @Value("${amqp.rabbitmq.password}")
    private String password;
    @Value("${amqp.rabbitmq.vhost}")
    private String vhost;
    @Value("${amqp.rabbitmq.exchange.name}")
    private String exchangeName;
    @Value("${amqp.rabbitmq.routing.key}")
    private String routingKey;
    /**
     * 批量发送消息的关键配置
     * */
    @Bean
    public TaskScheduler batchQueueTaskScheduler(){
        ThreadPoolTaskScheduler taskScheduler=new ThreadPoolTaskScheduler();
        //物理内核*2=线程池大小
        taskScheduler.setPoolSize(8);//任务池大小

        return taskScheduler;
    }

    /**
     * 创建原生的ConnectionFactory
     * 此factory由rabbitmq包提供
     * */
    @Bean
    public com.rabbitmq.client.ConnectionFactory amqpConnectionFactory(){
        com.rabbitmq.client.ConnectionFactory amqpFactory=new com.rabbitmq.client.ConnectionFactory();
        amqpFactory.setVirtualHost(vhost);
        amqpFactory.setHost(host);
        amqpFactory.setPort(port);
        amqpFactory.setUsername(username);
        amqpFactory.setPassword(password);
        return amqpFactory;
    }
    /**
     * 创建Spring提供的ConnectionFactoryBean
     * */
    @Bean
    public org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory(
            @Autowired
            com.rabbitmq.client.ConnectionFactory amqpConnectionFactory){
        org.springframework.amqp.rabbit.connection.ConnectionFactory factory=new PooledChannelConnectionFactory(amqpConnectionFactory);
        return factory;
    }

    @Bean
    public RetryTemplate retryTemplate(){
        //配置重试模板
        RetryTemplate template=new RetryTemplate();
        ExponentialBackOffPolicy policy=new ExponentialBackOffPolicy();
        policy.setInitialInterval(500);//重试的间隔时间
        policy.setMaxInterval(10000);//重试最大时间间隔
        policy.setMultiplier(10.0);//重试的倍数，如第一次重试间隔时间为500ms，第二次则加倍到5000ms，之后每次*10
        template.setBackOffPolicy(policy);
        return template;
    }
    /**
     * 配置交换机
     * 此交换机实例为Spring提供的Exchange接口的实现子类
     * */
    @Bean
    public Exchange exchange(){
        //使用主题模式有：TopicExchange;
        //使用直连模式有：DirectExchange;
        return new FanoutExchange(this.exchangeName);
    }

    @Bean // 最终消息的发送处理是由专属的模版类提供支持的
    public AmqpTemplate amqpTemplate(
            @Autowired
            RetryTemplate retryTemplate,
            @Autowired
            org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory,
            TaskScheduler batchQueueTaskScheduler
    ){
        //批量处理的相关配置
        int batchSize=20;
        int bufferedSize=4096;
        long timeout=10000;
        BatchingStrategy batchingStrategy= new SimpleBatchingStrategy(batchSize,bufferedSize,timeout);

       // RabbitTemplate template=new RabbitTemplate(connectionFactory);
        //批量生产者模板
        BatchingRabbitTemplate template=new BatchingRabbitTemplate(connectionFactory,batchingStrategy,batchQueueTaskScheduler);
        template.setExchange(this.exchangeName);
        template.setRetryTemplate(retryTemplate);
        return template;
    }
}
