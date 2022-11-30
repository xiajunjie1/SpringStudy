package com.maker.rabbitmq.producer.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

/**
 * Exchange使用Direct的模式进行数据的生产
 * */
public class TopicCrmProducer {
    private static final String ROUTING_KEY="crm.emp.add";//设置routing key
    private static final String SERVER="192.168.0.102";
    private static final int PORT=5672;
    private static final String USERNAME="jayjxia";
    private static final String PASSWORD="123456";
    private static final String VHOSTNAME="XjjVHost";//指定虚拟主机的名称
    private static final String EXCHANGE_NAME="com.jay.topic";//定义交换机名称，名称自定义

    public static void main(String[] args)throws Exception {
        topicProducer();
    }

    private static void topicProducer() throws Exception{
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost(SERVER);
        factory.setPort(PORT);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        factory.setVirtualHost(VHOSTNAME);//指定虚拟主机

        Connection connection=factory.newConnection();
        Channel channel=connection.createChannel();
        //进行Exchanger的创建，不需要在创建队列了
       channel.exchangeDeclare(EXCHANGE_NAME,"topic");//此处修改为topic模式
        long start=System.currentTimeMillis();
        for(int i=0;i<5;i++){
            String msg="【Topic CRM数据生产者】数据生产--"+i;
            //发布的时候关联上routing key
            channel.basicPublish(EXCHANGE_NAME,ROUTING_KEY, MessageProperties.PERSISTENT_TEXT_PLAIN,msg.getBytes());
        }
        long end=System.currentTimeMillis();
        System.out.println("消息发送完毕："+(end-start));
        channel.close();
        connection.close();
    }
}
