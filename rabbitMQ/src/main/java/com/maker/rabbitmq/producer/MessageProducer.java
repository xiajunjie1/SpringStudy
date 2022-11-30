package com.maker.rabbitmq.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 消息生产者类
 *  如果需要获得与RabbitMQ的连接，需要使用到ConnectionFactory进行配置
 * */
public class MessageProducer {
    private static final String SERVER="192.168.0.100";
    private static final int PORT=5672;
    private static final String USERNAME="jayjxia";
    private static final String PASSWORD="123456";


    public static void main(String[] args) {
        try{
            //produceMsg();
            produceMsgPresident();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void produceMsg()throws Exception{
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost(SERVER);
        factory.setPort(PORT);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        Connection connection=factory.newConnection();//创建完成连接
        Channel channel=connection.createChannel();//通过连接获取通道
        //所有的消息内容都保存在队列之中，现阶段rabbitMQ没有提供队列，所以需要自行创建
        channel.queueDeclare("xia",false,false,true,null);//通过通道来创建队列
        long start=System.currentTimeMillis();
        CountDownLatch latch=new CountDownLatch(100);
        //通过循环进行消息的发送
        for(int i=0;i<100;i++){
            int temp=i;

            new Thread(()->{
                String msg="【消息发送-"+temp+"】";
                try {
                    channel.basicPublish("","xia",null,msg.getBytes());

                    latch.countDown();//执行完后将latch值减1
                    System.out.println("【"+Thread.currentThread().getName()+"】发送消息");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            },"消息生产者"+i).start();
        }
        latch.await();//等待latch的值归0，相当于等待所有线程执行完毕
        long end=System.currentTimeMillis();
        System.out.println("【生产者执行时间】"+(end-start));
        System.out.println("消息发送完毕");
        channel.close();
        connection.close();
    }

    /**
     * 创建生产者，并且生产者存放在持久化队列之中
     * 此操作与之前的瞬时队列生产者的不同之处就在于在调用channel.queueDeclare方法
     * 的时候，第二个参数设为true即可。
     * 同样消费者也必须将对应的属性设为true
     *
     *  按照以上方式创建的生产者及生产者队列，在服务停止再次开启后，队列是可以进行恢复的，
     *  但是队列中的消息数据却仍然丢失，如果数据也需要恢复，那么生产者在发布数据的时候也要
     *  以持久化消息的模式进行发布
     *  具体做法为，在调用channel.basicPublish()方法发布消息的时候，第三个参数指定为MessageProperties.PERSISTENT_TEXT_PLAIN
     *
     * */
    private static void produceMsgPresident()throws Exception{
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost(SERVER);
        factory.setPort(PORT);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);

        Connection connection=factory.newConnection();//创建完成连接
        Channel channel=connection.createChannel();//通过连接获取通道
        //所有的消息内容都保存在队列之中，现阶段rabbitMQ没有提供队列，所以需要自行创建
        channel.queueDeclare("xia",true,false,true,null);//通过通道来创建队列
        long start=System.currentTimeMillis();
        CountDownLatch latch=new CountDownLatch(100);
        //通过循环进行消息的发送
        for(int i=0;i<100;i++){
            int temp=i;

            new Thread(()->{
                String msg="【消息发送-"+temp+"】";
                try {
                    //发送持久化消息
                    channel.basicPublish("","xia", MessageProperties.PERSISTENT_TEXT_PLAIN,msg.getBytes());

                    latch.countDown();//执行完后将latch值减1
                    System.out.println("【"+Thread.currentThread().getName()+"】发送消息");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            },"消息生产者"+i).start();
        }
        latch.await();//等待latch的值归0，相当于等待所有线程执行完毕
        long end=System.currentTimeMillis();
        System.out.println("【生产者执行时间】"+(end-start));
        System.out.println("消息发送完毕");
        channel.close();
        connection.close();
    }

}
