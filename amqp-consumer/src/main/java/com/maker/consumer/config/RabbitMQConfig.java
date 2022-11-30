package com.maker.consumer.config;

import com.maker.consumer.listener.RabbitMQMessageListener;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.PooledChannelConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

//@Configuration
//@PropertySource("classpath:config/amqp.properties")
public class RabbitMQConfig {
  //  @Value("${amqp.rabbitmq.host}")
    private String host;
   // @Value("${amqp.rabbitmq.port}")
    private Integer port;
   // @Value("${amqp.rabbitmq.username}")
    private String username;
   // @Value("${amqp.rabbitmq.password}")
    private String password;
    //@Value("${amqp.rabbitmq.vhost}")
    private String vhost;
    //@Value("${amqp.rabbitmq.queue.name}")
    private String queueName;
    //@Value("${amqp.rabbitmq.exchange.name}")
    private String exchangeName;
    //@Value("${amqp.rabbitmq.routing.key}")
    private String routingKey;
    /**
     * 创建原生的ConnectionFactory
     * 此factory由rabbitmq包提供
     * */
    //@Bean
    public com.rabbitmq.client.ConnectionFactory amqpConnectionFactory(){
        com.rabbitmq.client.ConnectionFactory amqpFactory=new com.rabbitmq.client.ConnectionFactory();
        amqpFactory.setVirtualHost(this.vhost);
        amqpFactory.setHost(this.host);
        amqpFactory.setPort(this.port);
        amqpFactory.setUsername(this.username);
        amqpFactory.setPassword(this.password);
        return amqpFactory;
    }
    /**
     * 创建Spring提供的ConnectionFactoryBean
     * */
    //@Bean
    public org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory(
          //  @Autowired
            com.rabbitmq.client.ConnectionFactory amqpConnectionFactory){
        org.springframework.amqp.rabbit.connection.ConnectionFactory factory=new PooledChannelConnectionFactory(amqpConnectionFactory);
            return factory;
    }
    /**
     * 声明队列
     * */
   // @Bean
    public org.springframework.amqp.core.Queue queue(){
        return new org.springframework.amqp.core.Queue(queueName,true,false,true);
    }

    /**
     * 注册监听器
     * */
   // @Bean
    public RabbitMQMessageListener rabbitMQMessageListener(){
        return new RabbitMQMessageListener();
    }

    /**
     * 将监听器放入监听容器中，注册监听容器
     * */
   // @Bean
    public SimpleMessageListenerContainer listenerContainer(
           // @Autowired
            RabbitMQMessageListener rabbitMQMessageListener,
           // @Autowired
            org.springframework.amqp.core.Queue queue,
           // @Autowired
            org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory
    ){
        SimpleMessageListenerContainer listenerContainer=new SimpleMessageListenerContainer(connectionFactory);//配置监听容器
        listenerContainer.setMessageListener(rabbitMQMessageListener);
        listenerContainer.setConcurrentConsumers(5);//并行的消费者数量
        listenerContainer.setMaxConcurrentConsumers(10);//最大并行的消费者数量
        listenerContainer.addQueues(queue);//追加队列
        listenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);//设置自动应答
        listenerContainer.initialize();//初始化监听容器
        return listenerContainer;

    }

   // @Bean
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
   // @Bean
    public Exchange exchange(){
        //使用主题模式有：TopicExchange;
        //使用直连模式有：DirectExchange;
        return new FanoutExchange(this.exchangeName);
    }
    /**
     * 绑定交换机和队列
     *
     */

   // @Bean
    public Binding binding(
          //  @Autowired
            Exchange exchange,
          //  @Autowired
            org.springframework.amqp.core.Queue queue
                           ){
        return BindingBuilder.bind(queue).to(exchange).with(this.routingKey).noargs();
    }

    /**
     * 将以上配置，进行整合，此类为核心类
     * */
   // @Bean
    public RabbitAdmin admin(

            org.springframework.amqp.core.Queue queue,

            Binding binding,

            RetryTemplate template,

            Exchange exchange,

            org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory

    ){
        System.err.println("@@@@@"+exchange+"---"+this.exchangeName);
        RabbitAdmin rabbitAdmin=new RabbitAdmin(connectionFactory);
        rabbitAdmin.setRetryTemplate(template);

        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareBinding(binding);
        return rabbitAdmin;
    }
}
