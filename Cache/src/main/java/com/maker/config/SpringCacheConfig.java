package com.maker.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.maker.cache.memcached.MemCachedManager;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 配置缓存管理器
 *  Cache和CacheManager都是org.springframework.cache包提供的
 * */
@Configuration
@EnableCaching //当前的应用要开启缓存
public class SpringCacheConfig {
    /*
    @Bean
    public CacheManager cacheManager(){
        //修改成Caffeine缓存管理器
        CaffeineCacheManager cacheManager=new CaffeineCacheManager();
        //SimpleCacheManager cacheManager=new SimpleCacheManager();
        Caffeine<Object,Object> caffeine=Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(3L, TimeUnit.SECONDS);
        //Set<Cache> caches=new HashSet<>();
        //caches.add(new ConcurrentMapCache("emp"));//CacheManager是通过名字来管理缓存实例的
        //caches.add(new ConcurrentMapCache("dept"));
        //caches.add(new ConcurrentMapCache("company"));

        cacheManager.setCaffeine(caffeine);
        cacheManager.setCacheNames(Set.of("emp"));
        return cacheManager;
    }*/
/**
 *
 * 另一种形式，使用CaffeineCache进行配置
 *
    @Bean
    public CacheManager cacheManager2() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        Set<Cache> caches = new HashSet<>();
        caches.add(new CaffeineCache("emp", Caffeine.newBuilder().build()));
        cacheManager.setCaches(caches); // 保存全部的缓存配置
        return cacheManager;
    }
    */

/**
 * 配置MemcachedManager，将Memcached与SpringCache整合起来
 * */
@Bean
public CacheManager cacheManager(){
    return new MemCachedManager();
    }
}
