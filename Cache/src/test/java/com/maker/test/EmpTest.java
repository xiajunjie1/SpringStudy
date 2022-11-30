package com.maker.test;

import com.maker.StarterSpringData;
import com.maker.po.Emp;
import com.maker.service.IEmpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;



@ContextConfiguration(classes = {StarterSpringData.class})
@ExtendWith(SpringExtension.class)
public class EmpTest {
    private static final Logger LOGGER= LoggerFactory.getLogger(EmpTest.class);
    @Autowired
    private IEmpService service;
    @Test
    public void testEdit(){
        Emp emp =new Emp();
        emp.setEid("xia");
        emp.setEname("夏俊杰");
        emp.setJob("家里蹲");
        emp.setSalary(10.0);
        service.edit(emp);
    }
    @Test
    public void getByIdTest(){
        Emp emp=service.getById("xia");
        LOGGER.info("雇员数据查询：eid={}、ename={}、job={}、salary={}",emp.getEid(),emp.getEname(),emp.getJob(),emp.getSalary());
    }
    @Test
    public void getByNameTest(){
        Emp emp=service.getByEname("俊");
        LOGGER.info("雇员数据查询：eid={}、ename={}、job={}、salary={}",emp.getEid(),emp.getEname(),emp.getJob(),emp.getSalary());
    }

    @Test
    public void deleteTest(){
        Boolean result=service.delete("jayj");
        LOGGER.info("删除结果为:{}",result);
    }

    /**
     * 调用业务层查询方法（已经配置过@Cacheable注解）
     * 可知两次查询，只向数据发送了一次查询命令
     *
     * 如果使用了Memcached这种No SQL数据库，缓存了一次后，再次执行查询，若数据未过期，那么
     * 不会在向数据库发送查询请求，而不同使用本机内存进行存储，重新执行后第一次查询仍然会向数据库发送
     * 查询请求
     * */
    @Test
    public void cacheTest1(){
        Emp emp=service.getById("xia");
        LOGGER.info("【第一次查询】雇员数据查询：eid={}、ename={}、job={}、salary={}",emp.getEid(),emp.getEname(),emp.getJob(),emp.getSalary());
        Emp emp2=service.getById("xia");
        LOGGER.info("【第二次查询】雇员数据查询：eid={}、ename={}、job={}、salary={}",emp2.getEid(),emp2.getEname(),emp2.getJob(),emp2.getSalary());
    }

    @Test
    public void cacheTest2(){
        Emp emp=service.getByEname("俊");
        LOGGER.info("【第一次查询】雇员数据查询：eid={}、ename={}、job={}、salary={}",emp.getEid(),emp.getEname(),emp.getJob(),emp.getSalary());
        Emp emp2=service.getByEname("俊");
        LOGGER.info("【第二次查询】雇员数据查询：eid={}、ename={}、job={}、salary={}",emp2.getEid(),emp2.getEname(),emp2.getJob(),emp2.getSalary());
    }
    /**
     * 缓存清理的测试
     *  在删除的同时将对应的缓存也进行清理
     *
     *  如果业务类中的删除方法，没有进行缓存清除，那么第一次查询和第二查询之间
     *  虽然进行了数据的删除，但是第二次仍然可查到数据（缓存数据）
     *  加上了相应的注解之后，第二次查询向数据库发送了查询请求，并且无法在查询到相应的数据了
     *
     *
     *
     *  多级缓存操作
     *      @Caching(
     *      put={
     *          @CachePut()
     *          ...
     *      })
     * */
    @Test
    public void deleteCacheTest(){
        Emp empA=service.getById("xia");//查询同时进行缓存，key为xia
        if(empA!=null){
            LOGGER.info("【第一次查询】雇员数据查询：eid={}、ename={}、job={}、salary={}",empA.getEid(),empA.getEname(),empA.getJob(),empA.getSalary());
        }
        else {
            LOGGER.info("【第一次查询】未查询到数据");
        }
       LOGGER.info("删除数据：{}",service.delete("xia"));//此时删除了eid为“xia”的数据，同时缓存中也将key为“xia”的数据给清除了
        //再次查询
        Emp empB=service.getById("xia");
        if(empB!=null){
            LOGGER.info("【第二次查询】雇员数据查询：eid={}、ename={}、job={}、salary={}",empB.getEid(),empB.getEname(),empB.getJob(),empB.getSalary());
        }
        else {
            LOGGER.info("【第二次查询】未查询到数据");
        }

    }
    /**
     * 多级缓存测试
     *  此处查询的多级缓存，是无法生效的
     *  多级缓存多用于缓存更新和缓存清理上
     */
    @Test
    public void CachingTest(){
        Emp emp=new Emp();
        emp.setEname("杰");
        emp.setEid("jie");
        Emp empA=service.getByEname2(emp);
        if(empA!=null){
            LOGGER.info("【第一次查询】雇员数据查询：eid={}、ename={}、job={}、salary={}",empA.getEid(),empA.getEname(),empA.getJob(),empA.getSalary());
        }
        else {
            LOGGER.info("【第一次查询】未查询到数据");
        }
       Emp empB=service.getById("jie");
        if(empB!=null){
            LOGGER.info("【第二次查询】雇员数据查询：eid={}、ename={}、job={}、salary={}",empB.getEid(),empB.getEname(),empB.getJob(),empB.getSalary());
        }
        else {
            LOGGER.info("【第二次查询】未查询到数据");
        }
    }
}
