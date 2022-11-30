package com.maker.main;

import com.github.benmanes.caffeine.cache.*;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Time;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 缓存技术
 *  之前在学习JPA的时候，也使用到了缓存技术，但是考虑到设计标准问题，所有ORM框架几乎都支持
 *  缓存管理操作，但是这些操作都是数据层上的缓存，而本次所学习的缓存概念是基于业务层上的缓存
 *
 *  单机缓存
 *      EHCache
 *      Google Guava：基于LUR算法实现，支持多种缓存过期策略
 *      Caffeine：对Guava组件的重写，虽然功能不如EHCache多，但是提供了最优的缓存命中率
 *          1.可以自动将数据加载到缓存当中，也可以采用异步的方式进行加载
 *          2.当基于频率和最近访问的缓存达到最大容量时，该组件会自动切换到基于大小的模式
 *          3.可以根据上一次缓存访问或上一次的数据写入来决定缓存的过期处理
 *          4.当某一条缓存数据出现了过期访问后可以自动进行异步刷新
 *          5.考虑到JVM的内存管理机制，所有的缓存key自动包含在弱引用当中，Value包含在弱引用或软引用当中
 *          6.当缓存被清理后将收到通知
 *          7.缓存数据写入可以传播到外部存储
 *          8.自动记录缓存数据被访问的次数
 *
 *
 *  分布式缓存
 *      Memcached
 *      redis
 *
 *
 *  手工缓存
 *      Caffeine组件中，最为核心的是Cache接口，所有与缓存有关的方法都是在这个接口中定义的
 *
 *  缓存数据的同步加载
 *      在之前进行数据加载的时候，使用到了Cache接口中提供的get()方法，这个方法可以结合Function接口
 *      在缓存数据已经失效之后进行数据的加载使用，而除此种加载操作的机制外，在缓存组件中还提供有一个较为
 *      特殊的CacheLoader接口，这个接口的触发机制有些不太一样，它所采用的依然是
 *      同步加载处理
 *      V load(K key)：实现数据的加载
 *      Caffeine提供的build方法，可以返回LoadingCache
 *
 *
 *  异步缓存
 *      buildAsync可以返回如下实例
 *          AsyncCache
 *              该接口为Cache的同级接口
 *
 *          AsyncLoadingCache
 *              AsyncCache的子接口
 *
 *
 *  缓存数据驱逐
 *      缓存数据不可能一直被保存，一旦时间到了后就会被清除。
 *      那么当缓存满了后呢？是不是也应该进行一些无用数据的驱逐。
 *      所有缓存数据是存在JVM的堆内存区域的
 *      驱逐策略
 *          1.JVM垃圾回收策略
 *              只能在同步缓存中使用
 *          2.容量回收策略（个数）
 *          3.时效回收策略(读取/权重)
 *          4.自定义回收策略
 *              Expiry接口
 *
 *
 *  缓存数据的删除与监听操作
 *      对于缓存数据的删除，Caffeine中提供两种方式
 *      一种是基于自动的驱逐策略完成的，另一种就是手工的实现方式
 *
 *
 *  CacheStats
 *      缓存的信息统计
 *      如缓存查询了多少次，有多少次查询准确等等
 *      默认情况下是未开启统计信息的
 *
 *
 *  缓存驱逐算法
 *      如果要考虑到数据的缓存，实际上最为核心的两个话题：一个就是缓存数据的命中率（命中率越高，缓存性能越好）
 *      另外一个就是缓存的驱逐，这个驱逐有两点：一个是驱逐的算法，另一个就是驱逐的具体实现
 *      缓存的算法有三种：
 *          FIFO(早期使用的缓存驱逐算法)
 *              该算法会存在缺页率问题，如果最早存储的数据属于热点数据，而因为队列长度问题
 *              会将该缓存数据给清除掉
 *          LRU（最近最久未使用）
 *              针对于每一个要操作的缓存项，通过数据最后一次访问的时间戳来进行排查，当缓存空间
 *              已经满员时，会将最久没有访问的数据进行清除。LRU算法是一种常见的缓存算法，在redis
 *              以及Memcached分布式缓存之中使用较多
 *
 *          LFU（内部扩充了TinyLFU算法与W-TinyLFU）
 *              最近最少使用算法
 *              在缓存中的数据在最近一段时间很少被访问到（访问频率低），其将来被访问到的可能性也很小，
 *              当缓存空间已满后，会优先清除最小访问频率的数据
 *
 *          TinyLFU算法
 *              LFU算法存在两个问题：
 *                  1、记录频次需要用到一个int或者long的数据类型，占用内存空间大
 *                  2、由于根据最近使用的频率来进行缓存的驱逐，所以旧数据的频率高于新数据，造成新数据一直无法缓存的问题
 *              TinyLFU优化了LFU算法，它提供了降频机制，当一个数据达到一个峰值后，就可以进行降频处理，这样就可以使得新数据可以存入到缓存当中
 *              它统计频率的时候，采用位的方式进行统计
 *
 *          W-TinyLFU算法（Windows-TinyLFU，Caffeine主要使用的就是该算法）
 *              它相比于LFU算，多提供了一个Windows的内存区域来进行缓存新的缓存数据（大概占缓存空间的1%）
 *              在主缓存区，又将其分为两个区域Protected区（80%，热点缓存数据）和Probation区（20%，即将驱逐的缓存数据）
 *
 *
 *  SpringCache
 *      SpringCache提供了业务层上缓存处理，之前在学习ORM框架的时候，缓存针对的都是数据层，而按照
 *      正规的设计思想来说，一个业务会牵扯到多个数据操作，而且一个业务展现也可能会存在有多个不同的数据层
 *      的缓存处理，相当于此时需要在不同的数据层中配置缓存，这样的设计就实在太麻烦了。
 *      搭建基本环境：
 *      1.引入SpringDataJPA相关依赖
 *      2.进行数据源和JPA以及事务的配置
 *      3.创建dao层IEmpDao接口
 *      4.创建service层IEmpService接口以及其实现类EmpServiceImpl
 *      5.在test下创建测试用例对service实例进行调用测试
 *
 *     在进行缓存实现的时候，Spring会考虑三种的缓存实现方案
 *      1.JDK内置实现（ConcurrentHashMap）
 *      2.第三方缓存组件（EHCache、Caffeine）
 *      3.分布式缓存组件（Memcached，Redis）
 *
 *     SpringCache之中为了便于缓存结构的管理，提供了两个核心标准接口
 *      1.Cache实现接口
 *      2.CacheManager管理接口
 *
 *      ConcurrentHashMap实现缓存管理
 *       1.去掉dao层的二级缓存选项
 *       2.创建SpringCacheConfig配置类
 *       3.在Service接口具体的方法上（主要是查询方法）配置@Cacheable注解
 *       4.在测试类中，进行缓存的测试
 *
 * @Cacheable
 *      在业务层中如果想使用缓存，则在方法上配置该注解，即可启用缓存
 *      在使用@Cacheable注解的时候，里面会有两个核心配置属性，一个缓存条件，一个缓存的排除
 *      如果想要进行这两个的配置，还需要用SpEL语法的标记
 *
 *  Caffeine实现缓存管理
 *      CaffeineCacheManager（要引入Spring Context Support依赖，否则没有此类）
 *      修改SpringCacheConfig配置类
 *
 *  缓存更新策略
 *      CachePut注解，一般可以在更新方法上配置该注解
 *      这种缓存的更新操作，其实并没有发生另一次数据查询，现阶段是仅仅在缓存内容上做了更新的处理，
 *      但是在高并发的情况，一般还是不建议去修改缓存
 *
 *  缓存清除策略
 *      按照常规的理解，缓存的数据应该与数据库之中的实体数据相对应，所以数据库之中的数据被删除后，对应
 *      的缓存数据理论上也要被删除。
 *      实际上很多系统，可能是缓存还在，但是数据已经不在了，因为缓存的更新相比较数据来讲更新肯定是较慢的，
 *      同时放在缓存中的很多数据，一般也不会轻易改变的
 *      如果要想实现缓存清楚，可以使用@CacheEvict注解完成
 *      修改IEmpService的接口
 *
 *
 *  Memcached缓存
 *      Memcached一般用于微小型的开发
 *      1.在linux虚拟机中安装memcached：yum -y install memcached
 *      2.查看memcached命令：memcached -h
 *      memcached常用命令
 *          -p：设置Memcached缓存服务的监听端口
 *          -m：Memcached应用分配的内存数量
 *          -u：运行memcached应用的用户
 *          -c：设置Memcached最大连接数量
 *          -d：采用后台进程的方式运行程序
 *     3.启动memcached服务：memcached -p 6030 -m 128m -c 512 -u root -d
 *     4.在防火墙上开通6030端口：
 *          firewall-cmd --zone=public --add-port=6030/tcp --permanent
 *          firewall-cmd --reload
 *
 *  Spring整合Memcached
 *      添加Memcached依赖：
 *          implementation 'com.whalin:Memcached-Java-Client:3.0.2'
 *      配置memcached.properties配置文件
 *      创建MemcachedConfig类，配置与Memcached连接的连接池和Memcached的客户端
 *      创建MemcachedTest测试类，进行连接测试
 *
 *  SpringCache整合Memcached
 *      SpringCache想要与Memcached进行整合，则需要按照SpringCache提供的标准来（Cache接口和CacheManager接口）
 *      由于SpringCache中并未提供Memcached适配的Cache实现类和CacheManager实现类，所以在此处需要我们自行创建
 *
 * */
public class CacheTest {
    private static final Logger LOGGER= LoggerFactory.getLogger(CacheTest.class);

    public static void main(String[] args) {
        try{
           //caffeineTest();
            //loadingCacheTest();
            //asyncCacheTest();
            //cacheDirverOutTest1();
            //cacheDirverOutTest2();
           // cacheDriverOutTest3();
           // cacheDriverOutTest4();
            //cacheDriverOutTest5();
            cacheStatsTest();

        }catch (Exception e){
            LOGGER.error("【main异常】{}",e.getMessage());
        }
    }

    /**
     * 使用Caffeine组件的核心接口Cache，完成基本操作
     * */
    private static void caffeineTest() throws Exception{
        Cache<String ,String> cache=Caffeine.newBuilder() //构建一个新的Caffeine实例
                .maximumSize(100) //最大缓存100个数据
                .expireAfterAccess(3L, TimeUnit.SECONDS) //设置无访问3秒后失效
                .build();//创建Cache接口实例
        cache.put("xia","夏");
        cache.put("jun","俊");
        cache.put("jie","杰");
        LOGGER.info("【未失效获取缓存】xia={}",cache.getIfPresent("xia"));
        TimeUnit.SECONDS.sleep(4);//延迟，使得缓存失效
       // LOGGER.info("【已失效获取缓存】xia={}",cache.getIfPresent("xia"));//获取结果为null
        //在获取缓存时，可以自定义获取的缓存的内容，Cache的获取也是同步的获取方式，即需要等待该函数执行完成返回值
        LOGGER.info("【已失效获取缓存】xia={}",cache.get("xia",(key)->{
            LOGGER.info("未获取到{}的缓存",key);
            try{
                TimeUnit.SECONDS.sleep(2);
            }catch (Exception e){
                throw new RuntimeException();
            }
            return "【EXPIRE】"+key;//自定义返回的缓存内容
        }));
    }
    /**
     * 使用getIfPresent获取缓存数据的时候，是不会调用LoadingCache接口中的load方法的
     * 只有当使用到getAll方法的时候，才算是加载数据，也才会调用load方法
     * 此处的缓存读取，属于是同步读取，必须执行完load方法才能一个个往下读
     * */
    private static void loadingCacheTest() throws Exception{
        LoadingCache<String ,String> cache=Caffeine.newBuilder() //构建一个新的Caffeine实例
                .maximumSize(100) //最大缓存100个数据
                .expireAfterAccess(3L, TimeUnit.SECONDS) //设置无访问3秒后失效
                .build((key)->{//此方法为CacheLoader中的load方法
                    LOGGER.info("【load方法】加载{}的缓存数据",key);
                    //此处的延迟够长，在读取数据的时候，读取到一个缓存不存在，就会加载load方法，这个时候不会去往下继续读取，等到此方法返回后才会继续读下一个
                    //缓存，由于在此处停止了10s，在读到下一个缓存的时候，本来应该存在的缓存已经过期了，所以，又会调用load方法
                    //此时又会在此处停止10s，无法继续往下读取
                    TimeUnit.SECONDS.sleep(10);
                    return "【load方法返回】"+key;
                });//创建LoadingCache接口实例,参数为CacheLoader，是一个函数式接口
            cache.put("jayj","夏");
            LOGGER.info("【未过期数据读取】{}",cache.getIfPresent("jayj"));
            TimeUnit.SECONDS.sleep(4);
            LOGGER.info("【过期数据读取】{}",cache.getIfPresent("jayj"));
            cache.put("jun","俊");
            cache.put("jie","杰");
            //加载数据，当加载的数据不存在的时候，会调用load方法，并且返回一个值供用户获取
            for(Map.Entry<String,String> entry:cache.getAll(List.of("jun","jayj","jie","xia")).entrySet()){
                LOGGER.info("【{}】={}",entry.getKey(),entry.getValue());
            }
    }
    /**
     * 异步缓存测试
     *  与之前同步读取缓存不同，当在执行load方法的时候，需要等待load方法读完，才会
     *  继续后面数据的获取，而异步方式获取缓存，则可以同时读取多个缓存数据
     *  所有的数据都经过了CompletableFuture包装
     * */
    private static void asyncCacheTest() throws Exception{
        AsyncLoadingCache<String,String> cache=Caffeine.newBuilder().maximumSize(100)
                .expireAfterAccess(3L,TimeUnit.SECONDS)
                .buildAsync((key,executor)->{//实现asyncLoad(K key, Executor executor)
                   return CompletableFuture.supplyAsync(()->{
                        LOGGER.info("【AsyncLoad】进行缓存数据的加载，当前的key={}",key);
                        try{
                            TimeUnit.SECONDS.sleep(10);//延迟10s
                        }catch(Exception e){
                            LOGGER.info("【CompleteableFuture异常】{}",e.getMessage());
                        }
                        return "【AsyncLoad返回】"+key;
                    });
                });
        cache.put("jayj",CompletableFuture.completedFuture("夏"));
        LOGGER.info("【未过期数据读取】{}",cache.getIfPresent("jayj").get());
        TimeUnit.SECONDS.sleep(4);
        //由于使用了CompletableFuture包装，所以不能返回null
        //由于此种方式并非加载缓存，所以不会触发asyncLoad方法
        //LOGGER.info("【过期数据读取】{}",cache.getIfPresent("jayj").get());

        cache.put("jun",CompletableFuture.completedFuture("俊"));
        cache.put("jie",CompletableFuture.completedFuture("杰"));
        //加载数据，当加载的数据不存在的时候，会调用asyncLoad方法，并且返回一个值供用户获取
        //由于采用的是异步加载的方式，所以所有的key会以多线程的方式异步加载，当加载到不存在的缓存时，调用asyncLoad，所有数据互不影响
        for(Map.Entry<String,String> entry:cache.getAll(List.of("jayj","jun","jie","xia")).get().entrySet()){
            LOGGER.info("【{}】={}",entry.getKey(),entry.getValue());
        }
    }
    /**
     * 缓存驱逐
     *  容量驱逐
     * */
    private static void cacheDirverOutTest1()throws Exception{
        Cache<String,String> cache=Caffeine.newBuilder().maximumSize(2)//容量驱逐设置
                .expireAfterAccess(3L,TimeUnit.SECONDS)
                .build();
        cache.put("xia","夏");
        cache.put("jun","俊");
        //已超过容量，会进行缓存的驱逐
        cache.put("jie","杰");
        //cache.put("jayj","夏俊杰");
        //cache.put("hello","helloworld");
        TimeUnit.MILLISECONDS.sleep(100);//清楚缓存数据需要时间，所以在此处加上延迟便于更好的观察
        LOGGER.info("【xia】{}",cache.getIfPresent("xia"));//null，被驱逐
        LOGGER.info("【jun】{}",cache.getIfPresent("jun"));
        LOGGER.info("【jie】{}",cache.getIfPresent("jie"));
        //LOGGER.info("【jayj】{}",cache.getIfPresent("jayj"));
       // LOGGER.info("【hello】{}",cache.getIfPresent("hello"));
    }

    private static void cacheDirverOutTest2()throws Exception{
        Cache<String,String> cache=Caffeine.newBuilder().maximumWeight(100)//权重驱逐设置，设置了权重就不能设置容量
                .weigher((key,value)->{//计算权重
                    //权重驱逐的原理是，没增加一个缓存项，都通过此方法计算一个权重，当增加的项目权重值
                    //相加超过最大权重值时，就进行驱逐
                    //可以根据key和value来计算权重
                    LOGGER.info("【{}】权重计算器",key);
                    return 40;//简单起见返回一个固定值
                })
                .expireAfterAccess(3L,TimeUnit.SECONDS)
                .build();
        cache.put("xia","夏");
        cache.put("jun","俊");
        //超过权重值
        cache.put("jie","杰");

        TimeUnit.MILLISECONDS.sleep(100);//清楚缓存数据需要时间，所以在此处加上延迟便于更好的观察
        LOGGER.info("【xia】{}",cache.getIfPresent("xia"));//null，被驱逐
        LOGGER.info("【jun】{}",cache.getIfPresent("jun"));
        LOGGER.info("【jie】{}",cache.getIfPresent("jie"));

    }
    /**
     * 时效回收策略
     *  写入后，过若干秒进行回收
     * */
    private static void cacheDriverOutTest3() throws Exception{
        Cache<String,String> cache=Caffeine.newBuilder().maximumSize(100)
                .expireAfterWrite(3L,TimeUnit.SECONDS) //设置写入后3s，缓存过期
                .build();
        cache.put("xia","夏");
        for(int i=0;i<3;i++){//只能获取到一次，因为每次延迟1.5s在读取，第二次读取的时候，距离写入时间已经过了3s了
            TimeUnit.MILLISECONDS.sleep(1500);//延迟1.5s
            LOGGER.info("【缓存获取】{}",cache.getIfPresent("xia"));
        }
    }
    /**
     * 自定义回收策略
     * */
    private static void cacheDriverOutTest4()throws Exception{
        Cache<String ,String> cache=Caffeine.newBuilder().maximumSize(100)
                .expireAfter(new Expiry<String, String>() {//自定义回收策略
                    @Override
                    public long expireAfterCreate(String key, String value, long currentTime) {//创建后若干秒回收，单位是纳秒
                        LOGGER.info("创建后失效时间计算，key={}、value={}",key,value);
                        return TimeUnit.NANOSECONDS.convert(2L,TimeUnit.SECONDS);
                    }

                    @Override
                    public long expireAfterUpdate(String key, String value, long currentTime, @NonNegative long currentDuration) {//更新后若干秒回收
                        LOGGER.info("更新后失效时间计算，key={}、value={}",key,value);
                        return TimeUnit.NANOSECONDS.convert(5L,TimeUnit.SECONDS);
                    }

                    @Override
                    public long expireAfterRead(String key, String value, long currentTime, @NonNegative long currentDuration) {//读取后若干秒回收
                        LOGGER.info("读取后失效时间计算，key={}、value={}",key,value);
                        return TimeUnit.NANOSECONDS.convert(2L,TimeUnit.SECONDS);
                    }
                }).build();
        cache.put("xia","夏");
        for(int i=0;i<3;i++){//三次都能成功读取到数据，因为每次都在重复读取，所以调用了expireAfterRead方法
            //每次过期时间都会重置到3s
            //但是如果第一次读取就超过了创建时间，那么就无法在读取到数据
            //TimeUnit.MILLISECONDS.sleep(1500);//延迟1.5s
            TimeUnit.MILLISECONDS.sleep(2500);//延迟2.5s
            LOGGER.info("【缓存获取】{}",cache.getIfPresent("xia"));
        }
    }

    /**
     * 手工清除缓存以及缓存数据删除的监听
     *      由运行结果可知，当执行手工清理缓存的时候，会开辟子线程触发缓存清理的监听方法
     *      而当采用的是内部驱逐策略时，如时效回收策略，当缓存超时后，在获取缓存
     *      的时候，会触发删除监听器
     * */
    private static void cacheDriverOutTest5()throws Exception{
        Cache<String,String> cache=Caffeine.newBuilder().maximumSize(100)
                .expireAfterWrite(2L,TimeUnit.SECONDS)
                .removalListener(new RemovalListener<String, String>() {
                    @Override
                    public void onRemoval(@Nullable String key, @Nullable String value, RemovalCause cause) {
                        LOGGER.info("【缓存数据清理】key={}、value={}、cause={}",key,value,cause);//输出缓存数据已经被清理的原因
                    }
                }).build();
        cache.put("xia","夏");
        cache.put("jun","俊");
        LOGGER.info("【缓存数据清理前】{}",cache.getIfPresent("xia"));
        cache.invalidate("xia");//手工清理缓存
        //LOGGER.info("【缓存数据清理后】{}",cache.getIfPresent("xia"));
        TimeUnit.SECONDS.sleep(3);//使缓存“jun”过期
        LOGGER.info("【获取超时缓存】{}",cache.getIfPresent("jun"));
        TimeUnit.SECONDS.sleep(30);//组织程序结束
    }

    /**
     *  缓存统计测试
     * */
    private static void cacheStatsTest()throws Exception{
        Cache<String,String> cache=Caffeine.newBuilder().maximumSize(100)
                .expireAfterWrite(1L,TimeUnit.SECONDS)
                .recordStats() //开启缓存统计
                .build();
        cache.put("xia","夏");
        cache.put("jun","俊");
        cache.put("jie","杰");
        //设置候选key
        String[] keys=new String[]{"xia","jun","jie","lee","wong"};
        Random random=new Random();
        for(int i=0;i<100;i++){
            //TimeUnit.MILLISECONDS.sleep(10);//延迟10ms
            new Thread(()->{
                String key=keys[random.nextInt(keys.length)];
                LOGGER.info("【{}】获取缓存，key={}、value={}",Thread.currentThread().getName(),key,cache.getIfPresent(key));
            },"查询线程-"+i).start();

        }
        TimeUnit.MILLISECONDS.sleep(100);
        CacheStats cacheStats=cache.stats();
        LOGGER.info("【CacheStats】缓存操作请求次数：{}",cacheStats.requestCount());
        LOGGER.info("【CacheStats】缓存命中次数：{}",cacheStats.hitCount());
        LOGGER.info("【CacheStats】缓存未命中次数：{}",cacheStats.missCount());
        LOGGER.info("【CacheStats】缓存命中率：{}",cacheStats.hitRate());
        LOGGER.info("【CacheStats】缓存驱逐次数：{}",cacheStats.evictionCount());
    }


}
