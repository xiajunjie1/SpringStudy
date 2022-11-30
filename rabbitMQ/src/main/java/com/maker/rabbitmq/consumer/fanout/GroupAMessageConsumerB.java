package com.maker.rabbitmq.consumer.fanout;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * A组的消费者B，即A组中有两个消费者
 * 根据测试的结果可知，在同一组中的消费者是交替消费队列中的数据的
 * 而每个不同的组中都能接收到完整的生产者生产的数据
 * */
public class GroupAMessageConsumerB {
    private static final String QUEUE_NAME="jay.a.group.queue";//队列名称，此队列并不是由生产者创建的
    private static final String SERVER="192.168.0.102";
    private static final int PORT=5672;
    private static final String USERNAME="jayjxia";
    private static final String PASSWORD="123456";
    private static final String VHOSTNAME="XjjVHost";//指定虚拟主机的名称
    private static final String EXCHANGE_NAME="com.jay.fanout";//定义交换机名称，名称自定义

    public static void main(String[] args) throws Exception {
        groupAConsumerB();
    }

    private static void groupAConsumerB() throws Exception{
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost(SERVER);
        factory.setPort(PORT);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        factory.setVirtualHost(VHOSTNAME);
        Connection connection=factory.newConnection();
        Channel channel=connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");//采用相同的Exchange
        //此队列并非生产者创建
        channel.queueDeclare(QUEUE_NAME,true,false,true,null);
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"");//将队列与Exchange绑定起来，路由key暂时未空
        Consumer consumer=new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg=new String(body);
                System.out.println("【GroupAConsumerB】接收数据："+msg);


            }
        };
        channel.basicConsume(QUEUE_NAME,consumer);//连接队列和消费者
    }
 }
