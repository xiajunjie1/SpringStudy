package com.maker.main;

import com.maker.SpringDataJPAStart;
import com.maker.dao.*;
import com.maker.po.Company;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * SpringDataJPA
 *  SpringDataJPA可以根据dao接口自动生成一个接口实现类
 *  并且将此接口的实现子类注册在Spring当中，这样就可以直接通过自动注入的方式
 *  获取到接口实例
 *  1.导入SpringDataJPA的依赖：
 *      implementation 'org.springframework.data:spring-data-jpa:2.7.2'
 *  引入SpringDataJPA的目的是不在项目中去使用任何XML文件（包括persistence.xml）
 *  SpringDataJPA会提供一个核心的配置类:LocalContainerEntityManagerFactoryBean
 *
 *  2.在数据库中创建一个Company表用作测试
 *  3.创建一个jpa.properties定义jap相关的配置，就是定义persistence.xml中的属性内容
 *  4.创建SpringDataJPA进行JPA相关配置
 *
 *
 *
 *  Repository接口
 *      所有的dao接口需要使用到@RepositoryDefinition注解进行配置，但是如果现在不想使用该注解
 *      也可以采用接口继承的模式
 *      让dao接口继承Repository<实体类,主键类>
 *
 *
 *  Repository方法映射
 *      之前基于数据层接口编写的CRUD的操作，是在是过于繁琐
 *      SpringDataJPA最推荐的做法是根据相应的方法名称实现相关的查询配置
 *      参考官方文档：
 *          https://docs.spring.io/spring-data/jpa/docs/2.7.5/reference/html/#jpa.query-methods.named-queries
 *          在SpringDataJPA中对于数据层来讲，方法以find开头表示进行数据查询操作，而后在find之后使用
 *          By进行字段的定义，如果现在要使用多个字段判断则使用And连接。
 *          如：findByLastnameAndFirstname==》Select ... from x where x.lastname = ?1 and x.firstname = ?2
 *
 *
 *  CrudRepository
 *      现在虽然可以由SpringData生成Dao的接口实例，但是这些实例并没有对
 *      CURD操作进行抽象，例如：现在有100张表，那么就需要重复定义100次CRUD操作
 *      为了简化这种操作，Repository接口中，提供了一个CurdRepository子接口，对CRUD进行抽象
 *      创建ICompanyDao3接口，继承CrudRepository接口
 *
 *  PagingAndSortingRepository
 *      分页查询
 *
 *  JpaRepository接口
 *      此接口把该有的功能全定义了。
 *      它除了传统CRUD外，可以实现数据的批量存储
 *      此接口还继承了PagingAndSortingRepository接口
 *      也就是说通过继承此接口，也可以实现分页相关的操作
 *
 *
 *
 * */

public class SpringDataJPA {
    private static final Logger LOGGER= LoggerFactory.getLogger(SpringDataJPA.class);
    //private ApplicationContext context;
    //private ICompanyDao3 companyDao3;
   /* public SpringDataJPA(){
        ApplicationContext context=new AnnotationConfigApplicationContext(SpringDataJPAStart.class);
        companyDao3=context.getBean(ICompanyDao3.class);
    }*/
    public static void main(String[] args)throws Exception {
      //new SpringDataJPA().findAllTest();
      //new SpringDataJPA().addTest();
        //new SpringDataJPA().updateTest();
        //SelectByIdTest();
         //  SelectByIdsTest();
        //deleteTest();
        //findByTest();
        //crudFindTest();
       //new SpringDataJPA().crud2LevelCacheTest();
        //findSplitPage();
        //jpaRepositoryAddTest();
        getById();

    }

    private  void findAllTest(){
        ApplicationContext context=new AnnotationConfigApplicationContext(SpringDataJPAStart.class);
        ICompanyDao companyDao=context.getBean(ICompanyDao.class);
        List<Company> alls=companyDao.findAll();
        for(Company company : alls){
            LOGGER.info("【公司数据】公司名称：{}、公司注册地：{}",
                    company.getName(), company.getPlace());
        }
    }

    private void addTest(){
        ApplicationContext context=new AnnotationConfigApplicationContext(SpringDataJPAStart.class);
        ICompanyDao companyDao=context.getBean(ICompanyDao.class);
        Company company = new Company();
        company.setName("xiajunjie");
        company.setCapital(300000.0);
        company.setNum(999);
        company.setPlace("武汉");
        Company result = companyDao.save(company);
        LOGGER.info("数据增加操作，增加后的数据ID为：{}", result.getCid());
    }


    public  void updateTest(){
        ApplicationContext context=new AnnotationConfigApplicationContext(SpringDataJPAStart.class);
        ICompanyDao companyDao=context.getBean(ICompanyDao.class);
        Company param=new Company();
        param.setCapital(78901.1);
        param.setCid(1L);
        param.setNum(78);
       int result= companyDao.editBase(param);
       LOGGER.info("影响的行数：{}",result);

    }

    private static void SelectByIdTest(){
        ApplicationContext context=new AnnotationConfigApplicationContext(SpringDataJPAStart.class);
        ICompanyDao companyDao=context.getBean(ICompanyDao.class);
        Company company=companyDao.findById(3L);
        LOGGER.info("【公司信息】公司编号：{}、公司名称：{}、注册资金：{}、人数：{}",company.getCid(),company.getName(),company.getCapital(),company.getNum());
    }

    private static void SelectByIdsTest(){
        ApplicationContext context=new AnnotationConfigApplicationContext(SpringDataJPAStart.class);
        ICompanyDao companyDao=context.getBean(ICompanyDao.class);
        List<Company> companys=companyDao.findByIds(Set.of(1L,2L,5L,6L));
        for(Company company : companys){
        LOGGER.info("【公司信息】公司编号：{}、公司名称：{}、注册资金：{}、人数：{}",company.getCid(),company.getName(),company.getCapital(),company.getNum());
    }
    }

    private static void deleteTest(){
        ApplicationContext context=new AnnotationConfigApplicationContext(SpringDataJPAStart.class);
        ICompanyDao companyDao=context.getBean(ICompanyDao.class);

        int result= companyDao.removeById(3L);
        LOGGER.info("影响的行数：{}",result);

    }
    /**
     * 利用方法映射的方式，查询相应的数据
     * */
    private static void findByTest(){
        ApplicationContext context=new AnnotationConfigApplicationContext(SpringDataJPAStart.class);
        ICompanyDao2 companyDao2=context.getBean(ICompanyDao2.class);
        Company company=companyDao2.findByCid(2L);
        LOGGER.info("【根据cid查询结果】公司编号：{}、公司名称：{}、注册资金：{}、人数：{}、注册地点：{}",company.getCid(),company.getName(),company.getCapital(),company.getNum(),company.getPlace());
        List<Company> result1=companyDao2.findByCidIn(Set.of(1L,2L,3L));
        companyListShow(result1,"查询cid在1,2,3中的Company数据");
        List<Company> result2=companyDao2.findByCapitalGreaterThan(100000.12);
        companyListShow(result2,"查询注册资金大于100000.12的Company数据");
        List<Company> result3=companyDao2.findByNameContainingOrderByPlaceDesc("xia");
        companyListShow(result3,"查询名字包含xia的所有Company数据，并且按照place字段降序排序");
        List<Company> result4=companyDao2.findByNameContainingAndPlaceContainingOrderByPlaceDesc("xia","武");
        companyListShow(result3,"查询名字包含xia并且注册地包含武的所有Company数据，并且按照place字段降序排序");




}

private static void crudFindTest(){
        ApplicationContext context=new AnnotationConfigApplicationContext(SpringDataJPAStart.class);
        ICompanyDao3 dao3=context.getBean(ICompanyDao3.class);
        Iterable<Company> iterable=dao3.findAll();
        for(Company company : iterable){
            LOGGER.info("【公司信息】公司编号：{}、公司名称：{}、注册资金：{}、人数：{}",company.getCid(),company.getName(),company.getCapital(),company.getNum());
        }
        Optional<Company> result=dao3.findById(2L);
        if(result.isPresent()){
            Company company=result.get();
            LOGGER.info("【公司信息ById】公司编号：{}、公司名称：{}、注册资金：{}、人数：{}",company.getCid(),company.getName(),company.getCapital(),company.getNum());
        }
}
/**
 * 不能在各自线程当中定义Spring的context变量
 * 否则它们会重复加载SpringJPA的相关配置，造成冲突
 * */
/*
public void crud2LevelCacheTest() throws Exception{

        Thread threadA=new Thread(()->{

            Optional<Company> result=this.companyDao3.findById(2L);
            if(result.isPresent()){
                Company company=result.get();
                LOGGER.info("【公司信息ById】公司编号：{}、公司名称：{}、注册资金：{}、人数：{}",company.getCid(),company.getName(),company.getCapital(),company.getNum());
            }
        },"线程A");

    Thread threadB=new Thread(()->{

        Optional<Company> result=this.companyDao3.findById(2L);
        if(result.isPresent()){
            Company company=result.get();
            LOGGER.info("【公司信息ById】公司编号：{}、公司名称：{}、注册资金：{}、人数：{}",company.getCid(),company.getName(),company.getCapital(),company.getNum());
        }
    },"线程B");
    threadA.start();
    TimeUnit.SECONDS.sleep(1);
    threadB.start();
}
*/

/**
 * 分页查询
 *
 * */
private static void findSplitPage(){
    ApplicationContext context=new AnnotationConfigApplicationContext(SpringDataJPAStart.class);
    ICompanyDao4 dao4=context.getBean(ICompanyDao4.class);
    int curpage=1;
    int linesize=2;
    Sort sort=Sort.by(Sort.Direction.DESC,"capital");//创建排序规则
    Pageable pageable= PageRequest.of(curpage-1,linesize,sort);//如果不需要排序，则不需要添加sort参数
    Page<Company> page=dao4.findAll(pageable);//获取查询数据
    LOGGER.info("总记录数：{}、总页数：{}",page.getTotalElements(),page.getTotalPages());
    for(Company company : page.getContent()){
        LOGGER.info("【公司信息】公司编号：{}、公司名称：{}、注册资金：{}、人数：{}、注册地点：{}",company.getName(),company.getCapital(),company.getNum(),company.getPlace());
    }
}
/**
 * 批量新增
 * */
private static void jpaRepositoryAddTest(){
    ApplicationContext context=new AnnotationConfigApplicationContext(SpringDataJPAStart.class);
    ICompanyDao5 dao5=context.getBean(ICompanyDao5.class);
    List<Company> all=new ArrayList<>();
    for(int i=0;i<50;i++){
        Company company=new Company();
        company.setName("jayjCompany-"+i+1);
        company.setNum(10+i);
        company.setCapital(100000.1+(i*100));
        company.setPlace("武汉");
        all.add(company);
    }
    dao5.saveAllAndFlush(all);
}
//对比两种根据id查询的区别
private static void getById(){
    ApplicationContext context=new AnnotationConfigApplicationContext(SpringDataJPAStart.class);
    ICompanyDao5 dao5=context.getBean(ICompanyDao5.class);
    Optional<Company> optional=dao5.findById(4L);//向数据库发出查询
    if(optional.isPresent()){
        //存在数据
        Company company=optional.get();
        LOGGER.info("【公司信息】公司编号：{}、公司名称：{}、注册资金：{}、人数：{}、注册地点：{}",company.getName(),company.getCapital(),company.getNum(),company.getPlace());

    }

    Company companyB=dao5.getReferenceById(2L);//并不会向数据库发出查询
    //加载数据的时候，向数据库发出查询，采用的懒加载的方式，可能会报错，因为此时与数据库的Session已经关闭
   // LOGGER.info("【公司信息】公司编号：{}、公司名称：{}、注册资金：{}、人数：{}、注册地点：{}",companyB.getName(),companyB.getCapital(),companyB.getNum(),companyB.getPlace());
}

private static void companyListShow(List<Company> result,String tip){
        for(Company company : result){
            LOGGER.info("【{}】公司编号：{}、公司名称：{}、注册资金：{}、人数：{}、注册地点：{}",tip,company.getCid(),company.getName(),company.getCapital(),company.getNum(),company.getPlace());
        }
}


}
