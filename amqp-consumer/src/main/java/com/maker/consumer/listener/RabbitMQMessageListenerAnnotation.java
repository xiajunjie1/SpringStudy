package com.maker.consumer.listener;

import com.maker.common.Dept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component//用注解注册该Bean
public class RabbitMQMessageListenerAnnotation {
    private static final Logger LOGGER= LoggerFactory.getLogger(RabbitMQMessageListenerAnnotation.class);
    @RabbitListener( //配置RabbitMQ消费者，此方法为接收消息的处理方法
                    //此配置方式摆脱强制实现接口
                    //以下内容，均在配置类中配置过
            queues = "xia.consumer.queue" ,//指定队列名称
            admin ="admin",
            containerFactory = "rabbitListenerContainerFactory"

    )
    public void handle(List<Dept> depts){
        LOGGER.info("*********************** 接收到新消息 ***********************");
        for (Dept dept : depts) {
            LOGGER.info("【接收消息】部门编号：{}、部门名称：{}、部门位置：{}",
                    dept.getDeptno(), dept.getDname(), dept.getLoc());
        }
    }
    /*
    public void handle(Dept dept){//接收对象
        LOGGER.info("【AnnotationRabbit】接收到信息,deptno={}、dname={}、loc={}",dept.getDeptno(),dept.getDname(),dept.getLoc());
    }

     */
    /*
    public void handle(String msg){
        LOGGER.info("【AnnotationRabbit】接收到信息：{}",msg);
    }*/


}
