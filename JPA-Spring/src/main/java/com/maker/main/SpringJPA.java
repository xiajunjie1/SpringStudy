package com.maker.main;

import com.maker.po.*;
import com.maker.util.DateUtil;
import com.maker.util.JpaUtil;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.jpa.AvailableHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * JPA
 *  Java持久化API，Hibernate就是JPA标准的实现
 *  JPA单元结构：
 *      Persistence（类）：持久化单元管理类，利用该类创建EntityManagerFactory接口实例
 *      EntityManagerFactory（接口）：实体管理工厂类，利用该类创建EntityManager实例
 *      EntityManager（接口）：实体对象管理，利用该接口可以创建事务以及数据操作
 *      EntityTransaction（接口）：事务管理接口，利用该接口可以提交或回滚事务
 *      Query（接口）：使用JPQL实现数据查询操作
 *
 *    1.导入相关依赖：
 *      mysql依赖
 *      jakarta.persistence:jakarta.persistence-api:3.1.0
 *      org.hibernate.orm:hibernate-core:6.1.0.Final
 *      org.hibernate.orm:hibernate-hikaricp:6.1.0.Final
 *      org.hibernate:hibernate-core-jakarta:5.6.9.Final
 *
 *      在JPA使用的过程中，是需要提供一个持久化类的，而这个持久化类需要使用到一些特殊的注解
 *      进行标记（JPA提供了所有可使用的注解），同时这个类结构要与数据库表结构完全对应
 *      2.在po包下，创建持久化PO类Course
 *
 *      3.在resource下创建META-INF/persistence.xml配置文件，必须得有META-INF
 *      4.由于项目之中存在日期操作，所以可以在util包下创建一个日期处理的工具类
 *      5.编写方法进行数据的插入
 *
 *
 *  JPA连接工厂
 *      为了简化相关的JPA操作，可以将创建EntityManagerFactory和EntityManager
 *      的操作封装成一个工具类。
 *      在util包下创建JpaUtil
 *
 *
 *
 *  DDL自动更新
 *      当数据库表结构出现修改后，可能出现实体类结构和数据库表结构不统一的情况
 *      在这种环境下，就需要让代码可以自动进行数据表的纠正
 *      更新策略：
 *          create：每次加载时，都会删除上一次创建的表，然后根据实体类创建一个新的数据表，
 *                  由于每次执行都会创建新的表，所以会导致原始数据的丢失
 *          create-drop：每次加载时会根据实体类生成数据表，但是EntityManagerFactory实例一关闭，
 *                          对应的数据表将自动删除
 *          update：最常用的DDL属性，第一次加载程序时会根据实体类自动创建数据表（此时必须首先创建好数据库），
 *                  在以后重新加载JPA程序时将根据实体类结构自动更新表结构，同时会保留原始数据记录。
 *                  要注意的是，当部署到服务器上时，表结构是不会马上被创建起来的，是要等到应用第一次运行起来后
 *                  才会创建
 *          validate：每次加载hibernate时，验证创建数据库表结构，只会和数据库中的表进行比较，不会创建新表，但是会插入新值
 *
 *          更新策略可以在persistence.xml中进行配置
 *          例如配置了update的更新策略，如果在Course PO类中添加了一个属性
 *          那么在执行hibernate代码的时候，会先执行alter语句将PO中新增的属性添加到数据库表结构上
 *          注意：如果是删除PO类中的某一属性，并不会触发alter操作
 *          如果在PO类中添加属性，但又不希望更新数据表，那么可以在这个属性上使用@Transient
 *
 *
 *
 *  JPA主键生成策略
 *      JPA中主键的生成，是基于@Id注解定义的
 *      AUTO：JPA自己选择合适的策略完成
 *      IDENTITY：采用数据库ID自增长方式，Oracle不支持
 *      SEQUENCE：采用序列方式生成主键，主要用于Oracle数据库
 *      UUID：采用UUID字符串为主键
 *      TABLE：通过指定数据库表生成主键，该策略便于移植数据库
 *
 *
 *  EntityManager数据操作
 *    public void persist(Object entity)：数据持久化（新增）
 *    public<T> T merge(T entity)：数据更新（不存在则新增）
 *    public void remove(Object entity)：数据删除
 *    public<T> T find(Class<T> entityClass,Object primarykey)：根据主键查询数据，不存在则返回null
 *    public<T> T getReference(Class<T> entityClass,Object primarykey)：根据ID查找数据，不存在则抛出异常
 *    public EntityTransaction getTransaction()：获取数据库事务
 *
 *
 *  JPQL语句
 *      在进行数据库操作的过程中，常规的技术手段就是SQL命令了，
 *      但是现在JPA属于ORM的开发组件，它现在也要实现各类的CURD的操作
 *      所以在Hibernate里面提供了一个JPQL的语法结构
 *      JPQL语法结构非常类似于SQL，如果想要使用JPQL的查询操作，则需要通过EntityManager
 *      接口，获取查询实例
 *
 *  SQL原生操作
 *      createNativeQuery(String sql)方法
 *      注意：使用此方法会造成与具体数据库的绑定（不同的数据SQL略有差别），所以会丧失数据库移植的特性
 *
 *  Criteria数据查询
 *      JPQL虽然比较方便，但是它的查询不符合面向对象的形式
 *      Criteria编程是基于：CriteriaBuilder和CriteriaQuery<Course>
 *          CriteriaBuilder中有很多方法，可以如果equals、or、gt、lt等等，都可以构建条件
 *
 *  JPA一级缓存（First Level Cache）
 *      在JPA中，考虑到数据层的性能问题，提供了缓存支持，而常见的缓存分为两类
 *      一级缓存、二级缓存
 *      一级缓存是一种始终都会存在的缓存结构，这种缓存会绑定在EntityManager接口实例之中，
 *      也就是说每一个用户处理的线程里面都会存在有缓存数据信息
 *
 *
 *  JPA对象状态
 *      在JPA开发的过程中，考虑到一些持久化对象的状态问题，所以针对持久化的对象实例不同的环境给出了四种不同的状态，
 *      每一种状态都有其各自的特点
 *      瞬时态：新实例化的对象，此对象并未实现持久化的存储（也可能还未分配ID），没有与持久化上下文（persistence context）建立任何关联
 *          数据新增时，在没有执行persist()之前就是瞬时态
 *
 *      持久态：数据库存在相应的ID的数据，该对象保存在一级缓存之中，
 *          EntityManager查询到的对象，就是属于持久态对象，该状态在EntityManager关闭之前都是有效的
 *
 *      游离态：数据库中存在相关的ID数据，但是该对象未与持久化上下文对象建立联系（EntityManager已关闭），此时该数据进行的修改不会影响到数据库
 *
 *      删除态：该对象与持久化上下文有联系，但其对应的数据库中的数据已删除
 *
 *  二级缓存
 *      如果此时有多个线程，对同一数据进行查询，那么这个时候一级缓存就没有任何意义了
 *      如果对于多线程查询同一个数据，想实现仅对数据库查询一次的操作，那么需要使用的二级缓存
 *      也就是说二级缓存是跨越多个EntityManager的查询操作
 *      如果想要引入二级缓存的处理，则要引入专门的缓存操作组件
 *      例如：
 *      分布式：redis、memcached
 *      单机：EHCache、Caffeine
 *
 *      1.引入EHCache缓存组件：
 *         org.hibernate.orm:hibernate-jcache:6.1.0.Final
 *         org.ehcache:ehcache:3.10.0
 *         javax.xml.bind:jaxb-api:2.3.1：由于使用的JDK17版本，所以需要导入java相关的依赖库
 *         com.sun.xml.bind:jaxb-impl:2.3.1
 *         com.sun.xml.bind:jaxb-core:2.3.1
 *         此处要引入JCache和EHCache，JCache是Java缓存的实现标准（注解等内容）
 *         而EHCache则是具体实现标准的组件
 *      2.修改persistence.xml文件，定义二级缓存相关组件和缓存模式
 *          缓存模式：
 *              ALL：所有实体类都被缓存
 *              NONE：所有实体类都不被缓存
 *              ENABLE_SELECTIVE：所有含有@Cacheable(true)的对象都被缓存
 *              DISABLE_SELECTIVE：除了含有@Cacheable(false)的对象都被缓存
 *              UNSPECIFIED：默认值，JPA产品默认值将被使用
 *
 *      3.在resources下创建一个ehcache.xml的EHCache配置文件
 *          缓存策略：
 *              LRU：清除最近较少使用的缓存数据
 *              LFU：清楚最近最少使用的缓存数据
 *              FIFO：清楚最老的缓存数据，容易造成热点数据被清除，造成缓存穿透
 *              缓存穿透：当热点数据被清除，在高并发的环境下，如果同时访问数据库，可能会造成数据库宕机
 *              这就是缓存穿透。
 *
 *       4.在需要缓存的实体对象类上，添加上@Cacheable注解
 *
 *
 *  查询缓存
 *      如果使用JPQL进行查询，想要启用二级缓存，还需要在persistence.xml中配置相应的属性
 *      相应的实体化类也需要实现对应的Serializable接口
 *
 * JPA数据锁
 *  锁的出现主要是体现在多线程修改数据的操作情况下，所有的数据库资源都是放在公共代码之中的，那么
 *  就意味着此部分的代码有可能就属于非线程安全的，这样就会有多个线程同时修改一条数据的可能性
 *  按照传统的数据库的概念来讲，每一条数据都会存在一个行锁，为了让这个锁的操作更加程序化，所以在
 *  JPA中才会提供两种锁机制：悲观锁、乐观锁
 *  锁的机制，一定要运行在事务之中
 *
 *  悲观锁：
 *      认为任何情况下都会存在有数据同步的操作问题，如果想要定义悲观锁，则需要通过
 *      LockModeType枚举类的一下常量进行配置
 *          PESSIMISTIC_READ：只要事务读实体，实体管理器就锁定实体，直至事务完成（事务回滚或提交），这种锁模式不会阻碍其他事务读取数据
 *          PESSIMISTIC_WRITE：只要事务更新实体，实体管理器就会锁定实体，这种锁模式，强制尝试修改数据的事务串行化，当多个并发更新事务出现更新失败几率较高时使用这种锁模式
 *          PESSIMISTIC_FORCE_INCREMENT：当事务读实体时，实体管理器就会锁定实体，当事务结束时会增加实体的版本属性，即使实体没有修改
 *          在Test下创建PessimisticTest测试类
 *
 *  乐观锁：
 *      认为有可能存在数据同步的操作，性能相比悲观锁更高
 *      乐观锁的实现，主要基于程序逻辑算法完成，在进行数据表定义的时候除了要定义核心字段以外，
 *      还需要定义一个版本编号，那么每次在读取的时候会读取到这个版本编号，并且每次更新的时候也会
 *      根据版本编号来实现
 *      1.修改数据库表：ALTER TABLE course ADD vseq INT DEFAULT 1，添加了一个vseq版本号字段
 *      2.修改实体类，增加相应的属性和特殊注解@Version
 *
 *
 *  JPA数据关联
 *      一对一
 *          在进行一对一映射的时候，表结构中必须设置外键，否则代码会出错
 *          一对一关联，在查询的时候，会将另一张关联的表也查询（left join）出来，如果并不需要
 *          另一张表的数据，那么这样的查询就没有意义了。
 *          使用懒加载的抓取模式，当前关联结构里面“FetchType.EAGER"，此操作就是抓取全部相关联的数据
 *
 *      一对多
 *          之所以在程序开发中不推荐使用一对多的关联结构进行定义，原因有两个：
 *              1.JPA对一对多结构定义的严谨性，必须提供有外键的支持
 *              2.性能问题，无论是否处于高并发的环境下，都会存在有性能问题
 *           在一对多的查询中，考虑到“多”方数据量比较庞大，所以默认使用的是懒加载的方式进行数据查询
 *           当需要用到“多”方数据的时候，才会再向数据库发出查询指令
 *           弊端：
 *              如果进行一对多查询，查询完成后关闭EntityManager实例，之后再进行“多”方数据加载
 *              这种时候就会抛出无Session的异常
 *
 *
 *      多对多
 *          多对多关联关系，例如成员和角色的关系就是一个多对多的关系
 *          多对多的关系需要引入一张中间表，中间表只保存表A和表B的主键信息
 *          并且将两张表的主键作为外键
 *          在程序中只需要创建两个实体类Jpamember和Jparole
 *
 * */
public class SpringJPA {
    private static final Logger LOGGER= LoggerFactory.getLogger(SpringJPA.class);
    public static void main(String[] args) throws Exception {

       // jpaAdd();
        //JpaUpdate();
        //findAndUpdate();
       // jpaGetReference();
       // jpaRemove();
        //jpqlSelectAll();
        //jpqlSelectById();
        //jpqlSelectSplit();
        //jpqlUpdate();
        //jpqlDelete();
        //sqlSelect();
        //criteriaQuery();
        //firstLevelCache();
        //managedTest();
        //managedCacheTest();
        //secondLevelCacheTest();
        //queryCacheTest();
      // new SpringJPA().pessimisticLockTest();
        //optimisticUpdateTest();
        //oneToOneSelect();
        //oneToOneAdd();
        //oneToManySelect();
        //oneToManyAdd();
        //manyToManySelect();
        manyToManyAdd();

    }

    private static void jpaAdd(){
        //持久单元的名称是在persistence.xml中进行配置的
       EntityManagerFactory factory = Persistence.createEntityManagerFactory("JayjJPA");
       EntityManager entityManager=factory.createEntityManager();
       entityManager.getTransaction().begin();//开启事务
        Course course=new Course();
        course.setCname("离散数学");
        course.setCredit(3);
        course.setNum(40);
        course.setStart(DateUtil.stringToDate("2015-03-10"));
        course.setEnd(DateUtil.stringToDate("2015-07-10"));
        entityManager.persist(course);//数据存储
        entityManager.getTransaction().commit();
        entityManager.close();
        factory.close();
    }
    /**
     * JPA更新数据，必须传入实体对象
     * 注意：实体对象中的属性必须都赋值，否则更新数据表的时候
     * 未被赋值的属性，相应的表字段也会被修改为null
     * */
    private static void JpaUpdate(){
        Course course=new Course();
        course.setCid(1L);
        course.setCname("高等数学II");
        course.setStart(DateUtil.stringToDate("2014-09-10"));
        course.setEnd(DateUtil.stringToDate("2015-01-10"));
        course.setCredit(4);
        course.setNum(40);
        EntityManager entityManager=JpaUtil.getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(course);//更新数据
        entityManager.getTransaction().commit();
        JpaUtil.close();
    }
    /**
     * 先查找需要更新的数据，然后在进行更新
     * 相比于传统的更新，多了一次查询，在性能上必然会存在限制
     * */
    private static void findAndUpdate(){
        EntityManager entityManager=JpaUtil.getEntityManager();
        entityManager.getTransaction().begin();
       Course course = entityManager.find(Course.class,2L);
       course.setCname("概率论");
       entityManager.merge(course);
       entityManager.getTransaction().commit();
       JpaUtil.close();
    }
    /**
     * getReference()和find()在查询存在的数据的时候，功能上是没有区别的
     * 但是如果查询不存在的数据，那么find()会返回null，而getReference()则会
     * 直接抛出异常
     * */
    private static void jpaGetReference(){
        EntityManager entityManager=JpaUtil.getEntityManager();
        //entityManager.getTransaction().begin();
        Course course= entityManager.getReference(Course.class,1L);
        LOGGER.info("【数据查询】{}",course);
        JpaUtil.close();
    }
    /**
     * 数据删除操作
     *  JPA数据删除只能先通过查询获取到相应的对象，然后在通过此对象进行数据的删除
     *  不支持自己构造一个对象，然后进行删除，所以此删除方式对比传统的删除对了一步查找的
     *  操作，这样必然会有严重的性能限制
     * */
    private static void jpaRemove(){
        EntityManager entityManager=JpaUtil.getEntityManager();
        entityManager.getTransaction().begin();
        Course course=entityManager.find(Course.class,2L);
        entityManager.remove(course);
        entityManager.getTransaction().commit();
    }
    /**
     * 使用JPQL查询所有的数据
     * */
    private static void jpqlSelectAll(){
        //此处查询的对象不是表名称，而是PO类对象的名称
        String jpql="select c from Course As c";
        //Query对象
       Query query = JpaUtil.getEntityManager().createQuery(jpql);
       List<Course> all= query.getResultList();
       for(Course c : all){
           LOGGER.info("{}",c);
       }
       JpaUtil.close();
    }
    /**
     * JPQL查询单个对象
     * */
    private static void jpqlSelectById(){
        //注意，此处的占位符必须加上索引顺序
        String jpql = "Select c from Course As c where c.cid=?1";
        //使用TypedQuery的好处就在于，可以在创建的时候指定对象类型，获取对象的时候不用再进行转型了
        TypedQuery<Course> query=JpaUtil.getEntityManager().createQuery(jpql,Course.class);
        query.setParameter(1,1L);
        Course course=query.getSingleResult();
        LOGGER.info("{}",course);
        JpaUtil.close();
    }

    private static void jpqlSelectSplit(){
        int curpage=2;
        int linsize=1;
        //此处查询的对象不是表名称，而是PO类对象的名称
        String jpql="select c from Course As c ";
        //Query对象
        Query query = JpaUtil.getEntityManager().createQuery(jpql);
        //开始查询
        query.setFirstResult((curpage-1)*linsize);
        //查询长度
        query.setMaxResults(linsize);
        List<Course> all= query.getResultList();
        for(Course c : all){
            LOGGER.info("{}",c);
        }
        JpaUtil.close();
    }
    /**
     * 使用JPQL语句更新数据
     * EntityManager提供的更新方式必须得传入完整的对象，无法进行单一属性的更新
     * 而是用JPQL则可以实现这一点
     * */
    private static void jpqlUpdate(){
        String jpql="Update Course As c Set c.credit=?1 where c.credit<?2";
        JpaUtil.getEntityManager().getTransaction().begin();
        Query query=JpaUtil.getEntityManager().createQuery(jpql);
        query.setParameter(1,5);
        query.setParameter(2,4);
        LOGGER.info("【执行更新操作】{}",query.executeUpdate());//执行更新
        JpaUtil.getEntityManager().getTransaction().commit();
        JpaUtil.close();
    }
    /**
     * 使用JPQL语句进行数据的删除
     * EntityManager提供的删除操作，必须要先查询获取到数据然后
     * 在将查询的数据对象传入来进行删除，这样的话，运行的效率是比较低的
     * 使用jpql的方式来进行操作，则可以避免掉这种情况
     * */
    private static void jpqlDelete(){
        //除了根据ID还可以根据各种条件来进行删除
        String jpql="delete from Course As c where c.cid =?1";
        JpaUtil.getEntityManager().getTransaction().begin();
        Query query=JpaUtil.getEntityManager().createQuery(jpql);
        query.setParameter(1,4L);
        LOGGER.info("【执行删除操作】{}",query.executeUpdate());
        JpaUtil.getEntityManager().getTransaction().commit();
        JpaUtil.close();
    }
    /**
     * 原生SQL的操作
     * */
    private static void sqlSelect(){
        String sql="Select * from course as c where c.cid=?1";
        Query query=JpaUtil.getEntityManager().createNativeQuery(sql);
        query.setParameter(1,1L);
        //用原生SQL查询到的结果，无法自动和PO类对象进行映射，它返回的结果是一个数组
        LOGGER.info("【查询结果】{}",query.getSingleResult());

    }

    /**
     * Criteria查询
     *  Criteria最好用的就是使用到in子句的查询
     *  如果有多个条件需要添加到where子句中，那么可以将多个条件存放在List集合中
     *  然后在where子句中传入集合，集合中的条件是用And串联的
     * */
    private static void criteriaQuery(){
        CriteriaBuilder builder=JpaUtil.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Course> criteriaQuery=builder.createQuery(Course.class);
        Root<Course> root=criteriaQuery.from(Course.class);//等价于Select ... from ...中的from子句
       Set<Long> cids=Set.of(1L,3L,4L,8L);
       //Predicate p1=builder.equal(root.get("cid"),1L);//构造查询条件
       //Predicate p2= builder.or(builder.gt(root.get("cid"),1L),builder.like(root.get("cname"),"%数学%"));//or条件连接，cid大于1且cname中包含数学的数据
        Predicate predicate=root.get("cid").in(cids);//IN查询配置
        criteriaQuery.where(predicate);
        Query query=JpaUtil.getEntityManager().createQuery(criteriaQuery);
        List<Course> courses=query.getResultList();
        for (Course course : courses){
            LOGGER.info("{}",course);
        }
    }
    /**
     * JPA一级缓存测试
     *
     * */
    private static void firstLevelCache(){
        Course course=JpaUtil.getEntityManager().find(Course.class,1L);
        LOGGER.info("【第一次查询】{}",course);
        course.setCredit(-99);//修改了缓存
        course=JpaUtil.getEntityManager().find(Course.class,1L);
        //此次查询，由于和第一次查询的内容是一样的，所以在缓存中存在数据，直接从缓存中拿到数据，不会访问数据库
        LOGGER.info("【第二次查询】{}",course);//结果就造成了拿到的数据credit为-99和数据库的数据不一致
       // JpaUtil.getEntityManager().refresh(course);//强制刷新，即再次从数据库中查询数据，相当于没有使用缓存
        LOGGER.info("【第三次查询】{}",course);

    }
    /**
     * 持久态测试
     *  测试可知，虽然并未调用任何更新的相关方法，但是当开启事务后
     *  修改持久态对象的属性，提交事务后，数据库的数据也发生了更新
     *  但是之后变为游离态后，修改了属性数据库的相应数据也不会受到影响
     * */
    private static void managedTest(){
        Course course=JpaUtil.getEntityManager().find(Course.class,5L);//持久态
        JpaUtil.getEntityManager().getTransaction().begin();//开始事务
        course.setCredit(2);
        JpaUtil.getEntityManager().getTransaction().commit();//提交事务，此时course对象变成了游离态
        course.setCredit(3);
        JpaUtil.getEntityManager().refresh(course);
        course=JpaUtil.getEntityManager().find(Course.class,5L);
        LOGGER.info("{}",course);
    }
    /**
     * JPA中，持久化状态会加入到缓存中，那么这个设计当中会存在一个问题
     *  那就是当批量插入多条数据中时，会有大量的数据在持久化时被加入到缓存之中
     *  如果数据量过大，会造成缓存的溢出
     *  解决方法是，在批量更新时，在插入一定条数后，就调用EntityManager中的flush()和clear()清空缓存
     * */
    private static void managedCacheTest(){
        Course course=new Course();//瞬时态对象
        course.setCname("计算机组成原理");
        course.setNum(40);
        course.setCredit(3);
        course.setStart(DateUtil.stringToDate("2015-03-10"));
        course.setEnd(DateUtil.stringToDate("2015-07-10"));
        JpaUtil.getEntityManager().getTransaction().begin();
        JpaUtil.getEntityManager().persist(course);//瞬时态变为持久态
        //刷新和清除缓存
        //JpaUtil.getEntityManager().flush();
        //JpaUtil.getEntityManager().clear();
        JpaUtil.getEntityManager().getTransaction().commit();

        //通过已持久化的对象cid来查询相应的数据
        Course course1=JpaUtil.getEntityManager().getReference(Course.class,course.getCid());
        //由观察可知，此时的course1并未查询数据库获取数据，因为course在转为持久态时已经存到了一级缓存之中，所以course1是直接从一级缓存中获取到相应数据的
        LOGGER.info("{}",course1);
    }

    /**
     * 由观察得知，在仅存在一级缓存的情况下，查询同样的数据内容
     * JPA向数据库发出了两次完全相同的查询指令，也就是说一级缓存
     * 没有起作用
     * */
    private static void secondLevelCacheTest()throws Exception{

        new Thread(()->{

           Course course= JpaUtil.getEntityManager().find(Course.class,3L);
           LOGGER.info("【{}】{},{}",Thread.currentThread().getName(),course,JpaUtil.getEntityManager());
           JpaUtil.close();
        },"查询线程1").start();

        new Thread(()->{


            Course course= JpaUtil.getEntityManager().find(Course.class,3L);
            LOGGER.info("【{}】{},{}",Thread.currentThread().getName(),course,JpaUtil.getEntityManager());
            JpaUtil.close();
        },"查询线程2").start();


    /*
        EntityManager entityManager=JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityManager entityManager1=JpaUtil.getEntityManagerFactory().createEntityManager();
        Course course1=entityManager.find(Course.class,3L);
        LOGGER.info("第一次查询：{}",course1);
        entityManager.close();
        Course course2=entityManager1.find(Course.class,3L);
        LOGGER.info("第二次查询：{}",course2);
        entityManager1.close();

    */

    }

    private static void queryCacheTest()throws Exception{




        EntityManager entityManager=JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityManager entityManager1=JpaUtil.getEntityManagerFactory().createEntityManager();
        Query query=entityManager.createQuery("Select c from Course As c where cid=:cid");
        query.setParameter("cid",3L);
        query.setHint(AvailableHints.HINT_CACHEABLE,true);//使用命中策略，一定要配置

        Course course1=(Course) query.getSingleResult();
        LOGGER.info("第一次查询：{}",course1);
        Query query2=entityManager.createQuery("Select c from Course As c where cid=:cid");
        query2.setParameter("cid",3L);
        query2.setHint(AvailableHints.HINT_CACHEABLE,true);//使用命中策略，一定要配置
        Course course2=(Course) query2.getSingleResult();
        LOGGER.info("第二次查询：{}",course2);
        entityManager1.close();



    }


    /**
     * 由测试可知，一旦在程序中加入了悲观写锁，SQL中会加入for update，并且它会独占数据
     * 直到事务完成，无论另一个线程中的锁是悲观写锁还是悲观读锁
     *
     * 如果两个线程中，加入的锁都是悲观读锁，那么SQL中，最后会加上for share，代表数据共享
     * 也就是说，无论谁加上锁，都不会影响到另一边的读取数据
     *
     * 另外，根据测试发现，如果此方法和相关方法使用静态方法的形式，那么就算加上了锁
     * 也是无效的，读写的线程会先上锁线程执行（上锁线程休眠了10s）
     * */
    public void pessimisticLockTest()  {
        //startLockThread(LockModeType.PESSIMISTIC_WRITE);//加上悲观写锁
        startLockThread(LockModeType.PESSIMISTIC_READ);//加了悲观读锁，读写线程先一步执行完成
        try{
            TimeUnit.MILLISECONDS.sleep(300);
        }catch (Exception e){
            LOGGER.error("{}",e.getMessage());
        }
        //readWriterThread(LockModeType.PESSIMISTIC_WRITE);
        readWriterThread(LockModeType.PESSIMISTIC_READ);
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
            //JpaUtil.getEntityManager().getTransaction().commit();//结束事务，只有结束事务，才会进行解锁;
            JpaUtil.getEntityManager().getTransaction().rollback();
            JpaUtil.close();

        },"读写线程-1");
        thread.start();
    }

    /**
     * 在更新的时候加上乐观锁
     * 加了乐观锁后，更新会执行两条SQL：
     * update course set vseq=? where cid=? and vseq=?
     * update course set  vseq=? where cid=? and vseq=?
     * */
    private static void optimisticUpdateTest(){
        JpaUtil.getEntityManager().getTransaction().begin();
        Course course=JpaUtil.getEntityManager().find(Course.class,3L,LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        course.setCname("Java开发实战");
        course.setCredit(2);
        JpaUtil.getEntityManager().getTransaction().commit();
        LOGGER.info("{}",course);
    }
    /**
     * Hibernate: select l1_0.uid,l1_0.password from Login l1_0
     * Hibernate: select d1_0.uid,d1_0.age,l1_0.uid,l1_0.password,d1_0.name from Details d1_0 left join Login l1_0 on l1_0.uid=d1_0.uid where d1_0.uid=?
     * */
    private static void oneToOneSelect(){
        String jpql="Select l from Login as l";
        //String jpql="Select d from Details as d";
        TypedQuery<Login> query=JpaUtil.getEntityManager().createQuery(jpql, Login.class);
        //TypedQuery<Details> query=JpaUtil.getEntityManager().createQuery(jpql, Details.class);
        List<Login> logins=query.getResultList();
        //List<Details> details=query.getResultList();

        for(Login l : logins){
            LOGGER.info("{}",l);
            LOGGER.info("{}",l.getDetails());
        }
        /*
        for(Details d : details){
            LOGGER.info("{}",d);
            LOGGER.info("{}",d.getLogin());
        }*/
        JpaUtil.close();
    }

    /**
     * Hibernate: insert into Login (password, uid) values (?, ?)
     * Hibernate: insert into Details (age, name, uid) values (?, ?, ?)
     * */
    private static void oneToOneAdd(){
        Login login=new Login();
        login.setUid("ronaldinho");
        login.setPassword("r10");
        Details details=new Details();
        details.setUid(login.getUid());
        details.setName("小罗");
        details.setAge(26);
        login.setDetails(details);
        JpaUtil.getEntityManager().getTransaction().begin();
        JpaUtil.getEntityManager().persist(login);
        JpaUtil.getEntityManager().getTransaction().commit();
        JpaUtil.getEntityManager().close();

    }
    /**
     * 由于采用的是懒加载的方式，所以一开始没有加载雇员信息的时候
     * 不会向数据库中发送查询命令获取数据，而遍历完部门信息后，
     * 需要读取雇员信息时，会再次向数据库发出查询指令
     *
     * */
    private static void oneToManySelect(){
        String jpql="Select d from Jpadept as d";
        JpaUtil.getEntityManager().getTransaction().begin();
        TypedQuery<Jpadept> query=JpaUtil.getEntityManager().createQuery(jpql,Jpadept.class);
        List<Jpadept> jpadepts=query.getResultList();
        for(Jpadept jpadept : jpadepts){
            LOGGER.info("【dept】部门编号：{}、部门名称：{}、位置：{}",jpadept.getDeptno(),jpadept.getDname(),jpadept.getLoc());
        }
        Jpadept dept=jpadepts.get(0);
        List<Jpaemp> emps=dept.getEmps();
        for(Jpaemp jpaemp : emps){
            LOGGER.info("【emp】员工编号：{}、员工名称：{}、薪资：{}",jpaemp.getEmpno(),jpaemp.getEname(),jpaemp.getSal());
        }
    }

    private static void oneToManyAdd(){
        JpaUtil.getEntityManager().getTransaction().begin(); // 开启JPA事务
        Jpadept dept = new Jpadept(55L, "开发部", "洛阳"); // 创建部门对象实例
        dept.setEmps(List.of(
                new Jpaemp(7839L, "夏", 910.0, dept),
                new Jpaemp(7859L, "俊", 840.0, dept),
                new Jpaemp(7879L, "杰", 760.0, dept))); // 部门和雇员之间的关系
        JpaUtil.getEntityManager().persist(dept); // 数据持久化
        JpaUtil.getEntityManager().getTransaction().commit();
        JpaUtil.close();
    }
    /**
     * 执行结果可知，此模式默认使用的也是懒加载的方式
     * 第一次日志输出的时候，没有对role进行查询，之后加载了role的List才对role进行了查询
     * Hibernate: select j1_0.mid,j1_0.password from Jpamember j1_0 where j1_0.mid=?
     * 【member】memberID：xia、password：jayjxia
     * Hibernate: select r1_0.mid,r1_1.rid,r1_1.name from member_role r1_0 join Jparole r1_1 on r1_1.rid=r1_0.rid where r1_0.mid=?
     * 【role】[RoleID：dept、name：部门管理, RoleID：emp、name：雇员管理]
     * */
    private static void manyToManySelect(){
        Jpamember member=JpaUtil.getEntityManager().find(Jpamember.class,"xia");
        LOGGER.info("【member】{}",member);
        LOGGER.info("【role】{}",member.getRoles());
    }
    /**
     * 多对多增加，只会更新主表和中间表，另一张表并不会被更新
     * */
    private static void manyToManyAdd(){
        Jpamember member=new Jpamember("jun","xjj123",new ArrayList<Jparole>());
        String[] rids=new String[]{"dept","emp"};
        JpaUtil.getEntityManager().getTransaction().begin();
        for(String rid : rids){
            member.getRoles().add(new Jparole(rid));//由于多对多更新只会更新主要的表（jpamember）和中间表，所以此处的role只需要id即可
        }
        JpaUtil.getEntityManager().persist(member);
        JpaUtil.getEntityManager().getTransaction().commit();
    }
}



