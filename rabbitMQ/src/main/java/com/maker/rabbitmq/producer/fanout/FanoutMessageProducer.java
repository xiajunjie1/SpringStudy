package com.maker.rabbitmq.producer.fanout;

import com.rabbitmq.client.*;

/**
 * Exchange使用fanout的模式进行数据的生产
 * */
public class FanoutMessageProducer{
    private static final String SERVER="192.168.0.102";
    private static final int PORT=5672;
    private static final String USERNAME="jayjxia";
    private static final String PASSWORD="123456";
    private static final String VHOSTNAME="XjjVHost";//指定虚拟主机的名称
    private static final String EXCHANGE_NAME="com.jay.fanout";//定义交换机名称，名称自定义

    public static void main(String[] args)throws Exception {
        fanoutProducer();
    }

    private static void fanoutProducer() throws Exception{
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost(SERVER);
        factory.setPort(PORT);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        factory.setVirtualHost(VHOSTNAME);//指定虚拟主机

        Connection connection=factory.newConnection();
        Channel channel=connection.createChannel();
        //进行Exchanger的创建，不需要在创建队列了
       channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        long start=System.currentTimeMillis();
        for(int i=0;i<5;i++){
            String msg="【fanout数据生产者】数据生产--"+i;
            channel.basicPublish(EXCHANGE_NAME,"", MessageProperties.PERSISTENT_TEXT_PLAIN,msg.getBytes());
        }
        long end=System.currentTimeMillis();
        System.out.println("消息发送完毕："+(end-start));
        channel.close();
        connection.close();
    }
}
