package com.maker.test;

import com.maker.StarterSpringData;
import com.whalin.MemCached.MemCachedClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@ContextConfiguration(classes = {StarterSpringData.class})
@ExtendWith(SpringExtension.class)
public class MemcachedTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemcachedTest.class);
    @Autowired
    private MemCachedClient client;
    @Test
    public void memcachedConnTest(){
        long expire=System.currentTimeMillis()+ TimeUnit.MILLISECONDS.convert(10,TimeUnit.MINUTES);//设置10分钟后过期
        LOGGER.info("设置Memcached缓存数据：{}",client.set("xia","jayj",new Date(expire)));//设置缓存k-v=>xia:jayj
        LOGGER.info("获取Memcached缓存数据：{}",client.get("xia"));
        client.flushAll();
    }
}
