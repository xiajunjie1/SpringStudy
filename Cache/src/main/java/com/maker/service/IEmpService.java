package com.maker.service;

import com.maker.po.Emp;
import org.springframework.cache.annotation.*;

//@CacheConfig(cacheNames = "emp")//可以在该接口上进行公共缓存名称的配置，这样的话，下面的注解中就可以将cacheNames = "emp"给省略掉
public interface IEmpService {
    @Caching(put = {
            @CachePut(cacheNames = "emp",key = "#emp.eid",unless = "#result==null"),//根据雇员id更新缓存
            @CachePut(cacheNames = "emp",key = "#emp.ename",unless = "#resule==null")//根据雇员的名字更新缓存
    })//设置多级缓存，进行更新缓存（缓存查询和删除也可以进行同样的配置）
    public Emp edit(Emp emp);
    @CacheEvict(cacheNames = "emp",key = "#eid")//根据eid进行缓存的删除
    public boolean delete(String eid);
    //sync和unless不能一起使用
    @Cacheable(cacheNames = "emp"
    , key = "#eid"
    ,condition = "#eid.contains('xia')"
            //, sync = true
            )//以传入的参数eid作为缓存项的key，并且只缓存eid包含xia的数据，写入缓存的时候加上同步处理，防止多线程读取和写入缓存时出现缓存穿透
    public Emp getById(String eid);
    @Cacheable(cacheNames = "emp"
    ,key = "#ename",
    unless = "#result.salary<5000")//不缓存查询结果中工资小于5000的数据
    public Emp getByEname(String ename);

    @Caching(cacheable = {//设置多级缓存，查询后，同时按照ename和eid设置缓存
            @Cacheable(cacheNames = "emp",key="#emp.ename"),
            @Cacheable(cacheNames = "emp",key="#emp.eid")
    })//Cacheable中的key是需要从参数列表中取值的，无法从返回结果中取值
   public Emp getByEname2(Emp emp);
}
