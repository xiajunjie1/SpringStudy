package com.maker.test;

import com.maker.po.Course;
import com.maker.util.JpaUtil;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.concurrent.TimeUnit;

public class PessimisticTest {
    private static final Logger LOGGER= LoggerFactory.getLogger(PessimisticTest.class);
    /**
     * 悲观锁测试
     * */
    @Test
    public void pessimisticLockTest() throws InterruptedException {
        LOGGER.info("程序开始");
        startLockThread(LockModeType.PESSIMISTIC_WRITE);//加上悲观写锁
        try{
            TimeUnit.MILLISECONDS.sleep(300);
        }catch (Exception e){
            LOGGER.error("{}",e.getMessage());
        }
        readWriterThread(LockModeType.PESSIMISTIC_WRITE);
        TimeUnit.SECONDS.sleep(Long.MAX_VALUE);//必须要加，不加的话，程序会在执行前就结束

    }
    /**
     * 加锁线程
     * */
    private void startLockThread(LockModeType type){
        Thread thread=new Thread(()->{
            JpaUtil.getEntityManager().getTransaction().begin();//开启事务，锁机制要在事务中运行
            Course course=JpaUtil.getEntityManager().find(Course.class,3L,type);//加锁
            LOGGER.info("【{}】名称：{}、学分：{}",Thread.currentThread().getName(),course.getCname(),course.getCredit());
            course.setCname("Java开发实战");
            course.setCredit(2);
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {

            }
            LOGGER.info("【{}】休眠结束，进行事务的回滚",Thread.currentThread().getName());
            JpaUtil.getEntityManager().getTransaction().rollback();//结束事务，只有结束事务，才会进行解锁});
            JpaUtil.close();
        },"上锁线程-1");
        thread.start();
    }
    /**
     * 进行读取或更新的线程
     * */
    private void readWriterThread(LockModeType type){
        Thread thread=new Thread(()->{
            JpaUtil.getEntityManager().getTransaction().begin();//开启事务，锁机制要在事务中运行
            Course course=JpaUtil.getEntityManager().find(Course.class,3L,type);//加锁
            LOGGER.info("【{}】名称：{}、学分：{}",Thread.currentThread().getName(),course.getCname(),course.getCredit());
            course.setCname("Spring开发实战");
            course.setCredit(3);
            LOGGER.info("【{}】更新完成",Thread.currentThread().getName());
            JpaUtil.getEntityManager().getTransaction().commit();//结束事务，只有结束事务，才会进行解锁;
            JpaUtil.close();

        },"读写线程-1");
        thread.start();
    }
}
