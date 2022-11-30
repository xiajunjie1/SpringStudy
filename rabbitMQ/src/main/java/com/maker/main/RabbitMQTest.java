package com.maker.main;
/**
 * 要使用RabbitMQ组件，首先必须要引入相关的依赖
 * implementation 'com.rabbitmq:amqp-client:5.15.0'
 *
 * 创建消息生产者
 *  在进行消息的生产的时候，一般需要提供有rabbitMQ的连接信息，例如：服务主机、
 *  端口、用户名和密码
 *  创建消息生产者类
 *
 *
 *  消息应答
 *      对于rabbitMQ队列中存储的消息，消费者可能因为消息丢失、延迟、错误导致消费端没有收到信息，
 *      针对这种情况RabbitMQ中就提供了一种应答机制。只有消费端发送了应答数据后，那么RabbitMQ才会进行
 *      消息的删除，而如果消费端未发送应答数据，这个时候RabbitMQ会进行消息的重投处理。
 *      而重投有可能造成RabbitMQ服务器上内存溢出的问题，所以就需要进行ACK信息的处理，有两种消息应答的处理机制
 *      1.自动应答机制
 *      2.手动应答机制
 *
 *  消息的持久化
 *      按照正常的理解思路：队列是保存消息的核心单元，如果某些消息已经发送到了队列之中，并且未消费的时候，那么该数据
 *      应该被持续保存下来，但是对于当前所使用的消息队列，只是一个普通的瞬时队列（重启就没了）。
 *      创建持久化队列：
 *         修改MessageProducer类
 *
 *  虚拟主机：
 *      当RabbitMQ用在多系统项目中时，每个系统可能都需要用到RabbitMQ组件
 *      在这种时候，可以通过RabbitMQ中创建虚拟主机的方式，提供给不同的系统进行使用
 *      具体的创建方式可以通过web控制台中admin->Virtual Hosts菜单进行创建
 *      程序使用虚拟主机，可以通过创建的ConnectionFactory中的factory.setVirtualHost(虚拟主机名);
 *      来进行指定（生产者和消费者都需要指定）
 *
 *  fanout广播模式
 *      在RabbitMQ中，最为重要的概念就是：Exchange(交换区)
 *      利用Exchange可以实现不同的交换模式
 *      fanout广播模式，针对不同的队列，进行广播，每个队列都可以接收到完整的生产者内容
 *      创建fanout程序包
 *
 *
 *  direct直连模式
 *      使用fanout使得同一交换区的不同队列可以收到消息，在进行队列和exchange绑定的
 *      时候，有一个routing key的配置，现在都是设为""，这就说明了每个队列的routing key
 *      都是相同的，这就实现了广播的消息处理
 *      如果现在要实现routing key，那么就需要使用到direct直连模式
 *
 *  topic主题模式
 *      主题模式与直连模式相比最大的一个特点在于可以基于表达式的匹配操作来实现RoutingKey的处理，
 *      在进行数据操作的时候，RoutingKey在直连模式的情况下是全匹配的处理方式，而Topic模式下可基于
 *      两个占位符进行自动匹配
 *      "*"：匹配0个或者1个单词；
 *      "#"：匹配0个、1个或多个单词。
 *
 *  Spring整合RabbitMQ
 *      创建三个子模块：amqp-commons、amqp-producer、amqp-consumer
 *      添加依赖的jar包：
 *          implementation group: 'org.springframework.amqp', name: 'spring-rabbit', version: '2.4.6'
 *          implementation group: 'org.apache.commons', name: 'commons-pool2', version: '2.11.1'
 *
 * */
public class RabbitMQTest {

}
