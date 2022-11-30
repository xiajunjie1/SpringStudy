package com.maker.config;

import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/memcached.properties")
public class MemcachedConfig {
    @Value("${memcached.server}")
    private String server;//服务器地址
    @Value("${memcached.weights}")
    private int weight;//权重
    @Value("${memcached.initConn}")
    private int initConn;//初始化连接的数量
    @Value("${memcached.minConn}")
    private int minConn;//最小维持的连接数量
    @Value("${memcached.maxConn}")
    private int maxConn;//最大连接数量
    @Value("${memcached.maintSleep}")
    private int maintSleep;//连接池维护周期
    @Value("${memcached.nagle}")
    private boolean nagle;//是否使用nagle算法
    @Value("${memcached.socketTO}")
    private int socketTO;//连接超时时间
    @Bean("socketIOPool")
    public SockIOPool initSockIOPool(){
        SockIOPool pool=SockIOPool.getInstance("memcachedPool");
        pool.setServers(new String[]{this.server});
        pool.setWeights(new Integer[]{this.weight});
        pool.setMinConn(this.minConn);
        pool.setMaintSleep(this.maintSleep);//维护间隔
        pool.setMaxConn(this.maxConn);
        pool.setInitConn(this.initConn);
        pool.setNagle(this.nagle);//由于此处并非高并发的集群环境，所以禁用Nagle算法，提升处理性能
        pool.setSocketTO(this.socketTO);//配置连接超时时间
        pool.initialize();
        return pool;
    }
    @Bean
    public MemCachedClient memCachedClient(){
        MemCachedClient client=new MemCachedClient("memcachedPool");//传入poolName初始化客户端中的pool属性

        return client;
    }

}
