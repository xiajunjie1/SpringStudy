package com.maker.rabbitmq.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MessageConsumer {
    /**
     * 此处的所有属性必须和生产者的相同，因为消费者消费也是基于队列来进行消费的
     * 那么就需要找到和生产者相同的队列，才可以对生产者生产的消息进行消费
     * */
    private static final String SERVER="192.168.0.100";
    private static final int PORT=5672;
    private static final String USERNAME="jayjxia";
    private static final String PASSWORD="123456";

    private static int count=1;
    public static void main(String[] args) {
        try {
            consumerTest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void consumerTest() throws Exception{
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost(SERVER);
        factory.setPort(PORT);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        Connection connection=factory.newConnection();
        Channel channel=connection.createChannel();
        channel.queueDeclare("xia",false,false,true,null);
        Consumer consumer=new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //消费处理，实际开发当中，此处是调用业务处理接口
                try{
                    TimeUnit.SECONDS.sleep(1);//每隔1s消费一次
                    System.err.println("【" + count++ + "】消息的消费处理");
                }catch (Exception e){

                }
                String message=new String(body);
                System.err.println("接收到数据："+message);
            }
        };//创建消费者
        channel.basicConsume("xia",consumer);//将消费者和队列关联起来
    }
    /**
     * 自动应答处理机制
     *  在建立rabbitMQ和消费者联系的时候，配置autoAck参数
     *
     * */
    private static void autoConsumerTest()throws Exception{
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost(SERVER);
        factory.setPort(PORT);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        Connection connection=factory.newConnection();
        Channel channel=connection.createChannel();
        //此处durable设为true代表获取消息的队列为持久队列
        channel.queueDeclare("xia",true,false,true,null);
        Consumer consumer=new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //消费处理，实际开发当中，此处是调用业务处理接口
                try{
                    TimeUnit.SECONDS.sleep(1);//每隔1s消费一次
                    System.err.println("【" + count++ + "】消息的消费处理");
                }catch (Exception e){

                }
                String message=new String(body);
                System.err.println("接收到数据："+message);
            }
        };//创建消费者
        channel.basicConsume("xia",true,consumer);//队列和消费者关联的时候加上一个布尔值参数，此参数代表是否自动应答

    }
    /**
     * 手动配置应答处理，在创建consumer对象的时候，进行设置
     * */
    private static void manualConsumerTest()throws Exception{
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost(SERVER);
        factory.setPort(PORT);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        Connection connection=factory.newConnection();
        Channel channel=connection.createChannel();
        channel.queueDeclare("xia",false,false,true,null);
        Consumer consumer=new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //消费处理，实际开发当中，此处是调用业务处理接口
                try{
                    TimeUnit.SECONDS.sleep(1);//每隔1s消费一次
                    System.err.println("【" + count++ + "】消息的消费处理");
                }catch (Exception e){

                }
                String message=new String(body);
                System.err.println("接收到数据："+message);

                //multiple参数，设置false代表对当前参数进行应答
                //如果设为true则表示对所有消息进行应答
                channel.basicAck(envelope.getDeliveryTag(),false);//配置手工应答
            }
        };//创建消费者
        channel.basicConsume("xia",consumer);//将消费者和队列关联起来
    }
}
