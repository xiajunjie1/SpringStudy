package com.maker.cache.memcached;

import com.whalin.MemCached.MemCachedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class MemCachedManager extends AbstractCacheManager {
    //存放所有的缓存
    private ConcurrentHashMap<String ,Cache> caches=new ConcurrentHashMap<>();
    private long expire= TimeUnit.MILLISECONDS.convert(10L,TimeUnit.MINUTES);//过期时间，单位毫秒
    @Autowired //利用Spring注入
    private MemCachedClient client;
    @Override
    protected Collection<? extends Cache> loadCaches() {
        return this.caches.values();
    }
    /**
     * SpringCache便是通过此方法来获取具体的Cache对象的
     * */
    @Override
    public Cache getCache(String name) {
        Cache cache=this.caches.get(name);
        if(cache==null){
            cache=new MemCached(this.client,name,this.expire);
            this.caches.put(name,cache);

        }
        return cache;
    }
}
