package com.maker.cache.memcached;

import com.whalin.MemCached.MemCachedClient;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class MemCached implements Cache {
    private MemCachedClient client;
    private String name;//缓存名称
    private long expire;//失效时间
    public MemCached(MemCachedClient client,String name,long expire){
        this.client=client;
        this.name=name;
        this.expire=System.currentTimeMillis()+expire;
    }
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this.client;
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper valueWrapper=null;
        Object value=this.client.get(key.toString());
        if(value!=null){
            valueWrapper=new SimpleValueWrapper(value);
            System.err.println("【get方法】"+valueWrapper.get());
        }
        return valueWrapper;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
       Object value=this.client.get(key.toString());
       if(type==null || !type.isInstance(value)){
           throw new IllegalStateException("缓存数据不是["+type.getName()+"]类型实例："+value);
       }
        return (T)value;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        T value = (T) this.get(key); // 获取数据
        if (value == null) {    // 数据为空
            FutureTask<T> futureTask = new FutureTask<>(valueLoader); // 异步数据加载
            new Thread(futureTask).start(); // 启动异步线程
            try {
                value = futureTask.get();
            } catch (Exception e) {}
        }
        return value;
    }




    @Override
    public void put(Object key, Object value) {
        this.client.set(key.toString(),value,new Date(this.expire));
    }

    @Override
    public void evict(Object key) {
        this.client.delete(key.toString());
    }

    @Override
    public void clear() {
        this.client.flushAll();
    }
}
