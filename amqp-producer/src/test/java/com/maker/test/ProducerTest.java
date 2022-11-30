package com.maker.test;

import com.maker.common.Dept;
import com.maker.producer.StartProducer;
import com.maker.producer.service.IMessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;

@ContextConfiguration(classes = StartProducer.class)
@ExtendWith(SpringExtension.class)
public class ProducerTest {
    @Autowired
private IMessageService service;
    @Test
    public void sendTest()throws Exception{
        service.send("xiajj-Test!!");
    }
    @Test
    public void sendObjectTest() throws Exception{
        Dept dept=new Dept();
        dept.setDeptno(1001L);
        dept.setDname("研发部");
        dept.setLoc("武汉");
        service.sendDept(dept);
    }
    @Test
    public void sendBatch() throws Exception{
        for(int i=0;i<100;i++){
            int temp=i;
            new Thread(()->{
                Dept dept=new Dept();
                dept.setDeptno(10L+temp);
                dept.setDname("研发部");
                dept.setLoc(Thread.currentThread().getName());
                try {
                    service.sendDept(dept);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            },"生产线程-"+i).start();
        }
        TimeUnit.SECONDS.sleep(5);
    }
}
