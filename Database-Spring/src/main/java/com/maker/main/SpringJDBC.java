package com.maker.main;

import com.maker.jdbc.config.DataSourceConfig;
import com.maker.jdbc.config.GlobalConfig;
import com.maker.jdbc.config.HikariCPDataSourceConfig;
import com.maker.jdbc.service.BookService;
import com.maker.jdbc.service.IBookService;
import com.maker.jdbc.vo.Book;
import com.mysql.cj.xdevapi.PreparableStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * SpringJDBC
 *  JDBC有四种连接模式：
 *      JDBC-ODBC（针对微软的开放数据库，已经确定不再使用）
 *      本地API驱动模式
 *      网络驱动程序（主要使用的就是该模式）
 *      纯Java驱动程序
 *
 *  SpringJDBC是属于JDBC的轻度包装组件，所以使用SpringJDBC可以简化JDBC传统开发
 *  里面繁琐的操作步骤
 *
 *
 *  DriverManagerDataSource
 *      SpringJDBC设计的时候提供了一个专属的DataSource接口实现类
 *      1.修改SpringDemo的dependencies.gradle和build.gradle添加相关依赖
 *      2.创建数据源的配置类DataSourceConfig
 *
 *
 *  HikariCP数据库连接池
 *      上面通过DriverManagerDataSource已经可以成功的获得连接，但是它的性能非常一般
 *      分析DriverManagerDataSource连接的流程
 *      该类的getConnection()是继承于父类的，在该抽象类中，此方法调用了getConnectionFromDriver(getUsername(), getPassword());
 *      getConnectionFromDriver方法中又调用了
 *      return DriverManager.getConnection(url, props);
 *      由此可见，每一次获取连接的时候，才会进行数据库连接的操作
 *      所以连接数据库的最佳方案肯定是基于数据库连接池的
 *      C3p0已经停止维护了，而HikariCP则可以作为它的替代组件来进行使用
 *      目前Spring中默认推荐的连接池组件是HikariCP
 *      1.修改SpringDemo的build.gradle文件，添加相关配置依赖
 *      2.在profile下创建dev/config/database.properties文件
 *      3.创建HikariCPDataSourceConfig配置类，将配置好的属性注入到数据源中
 *      4.编写方法进行测试
 *
 *
 *  JDBC Template
 *      jdbc Template可以通过继承的JdbcAccessor父类实现DataSource接口实例的配置
 *      在HikariCPDataSourceConfig配置类当中配置Jdbc Template
 *
 *  KeyHolder
 *      在MySQL的数据库中可以通过next()处理函数，获取我们当前所生成的id号
 *      这个id号主要是针对自动增长列，实际上这个功能的主要目的是为了解决增加
 *      数据时的ID返回处理，因为很多时候需要在数据增加成功之后对指定的ID进行控制，
 *      所以才提供了专属的处理函数
 *      在程序的开发之中，我们如果想要获取到ID的增长数据，SpringJDBC中提供了一个
 *      KeyHolder的接口
 *      KeyHolder是一个接口，那么按照常规的使用来讲，此时就需要定义一个完整的实现GeneratedKeyHolder子类，
 *      在开发的时候直接通过该子类进行对象的实例化处理即可。
 *
 *  数据库批处理
 *      public int[] batchUpdate(String... sql)：执行批处理的SQL命令
 *
 *  查询操作
 *      CRUD中除了更新操作，最核心的便是查询操作了。但是考虑到ORMapping框架的设计特点
 *      所有返回的数据都不应该再返回ResultSet，而是应该以对象的形式返回，所以在这个时候，SpringJDBC
 *      这种轻量级的开发框架之中，提供了一个RowMapper处理接口，它可以实现ResultSet向指定的实例对象的转换
 *      实体对象和数据库表的关系:
 *          类名称=表名称
 *          类属性=表字段
 *          类实例对象=单行数据
 *          类实例集合=多行数据
 *      mapRow()方法，可以接收查询结果每行数据的结果集，用户可以将指定列取出，
 *      并保存在目标VO实例之中
 *
 *  JDBC事务控制
 *      ACID事务原则
 *          原子性：整个事务的操作中，要么全部完成，要么全部回滚，不会停滞在中间任何一个阶段
 *          一致性：一个事务可以封装状态改变，事务必须始终保持系统处于一致的状态，不管在任何时间并发事务有多少
 *          隔离性/独立性：隔离状态执行事务，使他们好像是在系统给定时间内唯一执行的操作
 *          持久性：事务完成后，数据保存到数据库，不会再被回滚
 *
 *      Spring事务的处理架构
 *          Spring事务处理是在JDBC事务处理的基础上进行了封装
 *          PlatformTransactionManager接口，此接口为Spring事务处理提供的标准
 *          此接口中有getTransaction方法，该方法需要传入TransactionDefinition接口实例
 *          TransactionDefinition此接口中包含有事务传播控制的若干常量
 *          getTransaction方法返回的对象为TransactionStatus接口
 *          无论是什么框架或者组件，要想要接入到Spring中就必须实现PlatformTransactionManager接口
 *          所以不同的框架都有对应的接口实现子类
 *          编程式事务控制：
 *              1.在HikariCPDataSource中注册PlatformTransactionManager实例对象
 *              2.编写相应的方法进行测试
 *
 *      TransactionStatus
 *          在开启事务的时候，会返回一个TransactionStatus接口实例（DefaultTransactionStatus），
 *          而后在提交或回滚的时候都需要针对于指定的status实例进行处理
 *          status得到的是一个事务的处理标记，而后Spring依据此标记管理Spring事务
 *          TransactionStatus中包含以下常用方法
 *              public boolean hasSavepoint()
 *              public boolean isCompleted()
 *              public boolean isRollbackOnly()
 *              public void setRollbackOnly()
 *              public void rollbackToSavepoint(Object savepoint) throws TransactionException
 *              public Object createSavepoint() throws TransactionException
 *              public void releaseSavepoint(Object savepoint) throws TransactionException
 *              public boolean isNewTransaction() //判断事务是否开启
 *
 *      事务隔离级别
 *      事务中可能存在的问题：
 *          1.脏读：一个事务可以读到另一个更新事务未提交的更新数据（由于未提交，所以可能回滚）
 *          2.不可重复读：事务A中，对一条数据多次读取，在读取的过程中，事务B更新了该数据，事务A读到更新后的数据（一个事务中，多次多去一条数据获得的结果不同）
 *          3.幻读：事务A多次查询满足同一条件的数据，在查询的过程中，事务B又插入了一条满足条件的数据，事务A再次读取发现多了一条数据
 *          （例如，事务A查询某一数据，发觉不存在，准备插入某一特定的数据，而插入之前，事务B将特定的数据插入进去了，事务A再次插入会发现此数据已经存在，或者某一数据被事务B删除，事务A查询时发现还存在该数据，但实际在insert的时候还是可以插入进去，这种情况便是幻读）
 *       隔离级别：
 *          1) DEFAULT （默认）
 *              这是一个PlatfromTransactionManager默认的隔离级别，使用数据库默认的事务隔离级别。另外四个与JDBC的隔离级别相对应。
 *              MYSQL数据库默认隔离级别为REPEATABLE_READ
 *              查看MySQL数据库隔离级别的方法：show variables like %transaction_isolation%
 *          2) READ_UNCOMMITTED （读未提交）
 *              这是事务最低的隔离级别，它允许另外一个事务可以看到这个事务未提交的数据。这种隔离级别会产生脏读，不可重复读和幻像读。
 *          3) READ_COMMITTED （读已提交）
 *              保证一个事务修改的数据提交后才能被另外一个事务读取，另外一个事务不能读取该事务未提交的数据。这种事务隔离级别可以避免脏读出现，但是可能会出现不可重复读和幻像读。
 *          4) REPEATABLE_READ （可重复读）
 *              这种事务隔离级别可以防止脏读、不可重复读，但是可能出现幻像读。它除了保证一个事务不能读取另一个事务未提交的数据外，还保证了不可重复读。
 *           5) SERIALIZABLE（串行化）
 *              这是花费最高代价但是最可靠的事务隔离级别，事务被处理为顺序执行。除了防止脏读、不可重复读外，还避免了幻像读。
 *
 *
 *      事务的传播机制
 *          事务开发是和业务层有直接关联的，在进行开发的过程之中，很难出现业务层之间不互相调用的场景，例如：
 *          存在一个A业务处理，但是A业务处理的时候可能调用B业务，如果两个业务之中，存在各自的事务的机制，那么这个时候
 *          就需要进行事务的有效传播
 *          传播机制：
 *          1) REQUIRED（默认属性）
 *              子业务支持直接支持当前父级事务，如果当前父业务之中没有事务，则创建一个新的事务，
 *              如果父业务中存在事务，，则合并为一个完整的业务
 *              不管何时，只要进行了业务调用，都需要创建一个新的事务，此机制是最为常用的传播机制
 *
 *          2) MANDATORY
 *              支持当前父级事务，如果当前没有父级事务，就抛出异常。必须有父级事务
 *          3) NEVER
 *              以非事务方式执行，如果当前存在事务，则抛出异常。
 *          4) NOT_SUPPORTED
 *              以非事务方式执行操作，如果当前存在事务，就把当前事务挂起。
 *              在进行其他业务调用的时候，不管是否存在有事务，统一关闭
 *          5) REQUIRES_NEW
 *              新建事务，如果当前存在事务，把当前事务挂起。
 *              子业务有独立的事务处理，不管是否有父级事务，都不影响子业务事务的运行
 *          6) SUPPORTS
 *              支持当前父业务的事务，如果当前父业务没有事务，就以非事务方式执行。
 *          7) NESTED
 *              支持当前事务，新增Savepoint点，与当前事务同步提交或回滚。
 *              嵌套事务一个非常重要的概念就是内层事务依赖于外层事务。外层事务失败时，会回滚内层事务所做的动作。而内层事务操作失败并不会引起外层事务的回滚。
 *              所有的事务统一交给调用业务处理，例如父业务出错回滚了，即使当前业务正确，也会跟着父业务回滚
 *
 *          传播机制一般通过TransactionDefinition实例对象进行配置
 *
 *      只读事务的控制
 *          使用只读事务进行处理的时候，所有的数据不运行进行更新的操作，只能够进行读取的控制操作
 *          在进行只读配置的时候，可以直接通过TransactionDefinition接口提供的setReadOnly()方法
 *          进行配置
 *
 *
 *      @Trasactional注解
 *          通过Transactional注解配置事务
 *          1.通过XML配置文件，引入相关命名空间tx
 *          2.在xml文件中，开启事务注解配置
 *          3.创建BookService业务类，并且在相应的方法上加上@Transactional注解
 *
 *
 *      AOP事务处理
 *          使用@Transactional注解，会存在一个弊端
 *          就是当业务层中需要使用的事务的方法比较多的时候，添加注解也会变得很麻烦
 *          所以不应该将事务有关的代码直接硬编码到项目之中，采用注解的形式一定不是当前推荐的形式
 *          最佳的形式应该是通过AOP的切面管理，即：通过AOP表达式实现事务的控制。
 *          1.在xml配置文件中配置事务相关的切面，并通过AOP织入到业务层的相关方法中
 *          2.创建IBookService接口及其对应的实现类
 *          3.在本类中编写测试方法，调用业务层方法
 *
 *      基于Bean配置类配置AOP事务处理
 *          1.在config包下创建TransactionAdviceConfig类
 *
 *
 *
 * */
public class SpringJDBC {
    private static final Logger LOGGER= LoggerFactory.getLogger(SpringJDBC.class);
    public static void main(String[] args) throws Exception{
        //dataSourceTest();
        //HikariDataSourceTest();
        //JdbcTemplateTest();
        //keyHolderTest();
        //batchUpdateTest1();
        //batchUpdateTest2();
        //queryByIdTest();
        //queryAll();
        //querySplitTest();
        //queryCountTest();
        //transactionTest();
        //isolationTest();
        //TransactionalTest();
        aopTransactionTest();
    }
    private static ApplicationContext openSpring(Class<?> ... clazz){
        AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext(clazz);
        return context;
    }
    /**
     * 测试配置的数据源对象能否正常的获取
     * */
    private static void dataSourceTest()throws Exception{
        AnnotationConfigApplicationContext context= (AnnotationConfigApplicationContext) openSpring(DataSourceConfig.class);
        DataSource dataSource=context.getBean(DataSource.class);
        LOGGER.info("获取到的数据源对象：{}",dataSource);
        LOGGER.info("获取到的连接对象：{}",dataSource.getConnection());



    }

    private static void HikariDataSourceTest()throws Exception{
        AnnotationConfigApplicationContext context= (AnnotationConfigApplicationContext) openSpring(HikariCPDataSourceConfig.class);
        DataSource dataSource=context.getBean(DataSource.class);
        LOGGER.info("获取到的数据源对象：{}",dataSource);
        LOGGER.info("获取到的连接对象：{}",dataSource.getConnection());

    }
    /**
     * update单参SQL就是使用的statement进行的更新操作
     * 这种操作并不安全，而update(String ,Object...)方法是可以带参数的
     * 该操作使用的是PrepareStatement
     * */
    private static void JdbcTemplateTest(){
        ApplicationContext context=openSpring(HikariCPDataSourceConfig.class);
        JdbcTemplate template=context.getBean(JdbcTemplate.class);
        //String sql="insert into book(title,author,price) Values('jayj ','xia','101')";
        //LOGGER.info("【新增数据库】：{}",template.update(sql));
        String sql="insert into book(title,author,price)Values(?,?,?)";
        //使用可变参数的update方法，底层使用的是PrepareStatement执行的SQL
        LOGGER.info("【新增数据库】：{}",template.update(sql,"SpringTest","xia",99.99));
    }
    /**
     * 使用KeyHolder获取新增id的值
     * 要使用KeyHolder就需要使用update( PreparedStatementCreator psc,KeyHolder keyHolder)方法
     *  PreparedStatementCreator是一个函数式接口
     * */
    private static void keyHolderTest(){
        ApplicationContext context=openSpring(HikariCPDataSourceConfig.class);
        JdbcTemplate template=context.getBean(JdbcTemplate.class);
        String sql="insert into book(title,author,price)Values(?,?,?)";
        KeyHolder keyHolder=new GeneratedKeyHolder();
       int count= template.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                //Statement.RETURN_GENERATED_KEYS一定要加上，否则会报错
                PreparedStatement pstmt=con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1,"SpringBoot");
                pstmt.setString(2,"jajjxia");
                pstmt.setDouble(3,88);
                return pstmt;
            }
        }, keyHolder);
       LOGGER.info("数据库更新条数：{},新增ID={}",count,keyHolder.getKey());

    }
    /**
     * 利用BatchPreparedStatementSetter进行数据的批量处理
     *
     * */
    public static void batchUpdateTest1(){
        ApplicationContext context=openSpring(HikariCPDataSourceConfig.class);
        JdbcTemplate template=context.getBean(JdbcTemplate.class);
        List<String> titles= List.of("SSM","Java SE","Java Web","Spring Cloud");
        List<Double> prices=List.of(66.1,77.2,88.3,99.4);
        String sql="insert into book (title,author,price) Values(?,?,?)";
        int[] result=template.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                //设置占位符值
                ps.setString(1,titles.get(i));
                ps.setString(2,"xiajunjie");
                ps.setDouble(3,prices.get(i));
            }

            @Override
            public int getBatchSize() {
                return titles.size();
            }
        });
        LOGGER.info("数据库更新条数：{}", Arrays.toString(result));

    }
    /**
     * 利用List<Object[]>进行批量插入
     * */
    private static void batchUpdateTest2(){
        ApplicationContext context=openSpring(HikariCPDataSourceConfig.class);
        JdbcTemplate template=context.getBean(JdbcTemplate.class);
        List<Object[]> params=List.of(
                new Object[]{"数据结构","xia",101.1},
                new Object[]{"操作系统原理","jun",99.1},
                new Object[]{"计算机组成原理","jie",77.88},
                new Object[]{"计算机网络","jayj",88.88}
        );
        String sql="insert into book(title,author,price)Values(?,?,?)";
        int[] result=template.batchUpdate(sql,params);
        LOGGER.info("批量插入数据情况：{}",Arrays.toString(result));

    }
    /**
     * 查询单条数据
     * */
    private static void queryByIdTest(){
        ApplicationContext context=openSpring(HikariCPDataSourceConfig.class);
        JdbcTemplate template=context.getBean(JdbcTemplate.class);
        String sql="Select bid,title,author,price from book where bid=?";
        Book book=template.queryForObject(sql,new BookRowMapper(),2);//查询编号为2的book信息
        LOGGER.info("查询到的book信息为：{}",book);
    }
    /**
     * 查询全部信息
     * */
    private static void queryAll(){
        ApplicationContext context=openSpring(HikariCPDataSourceConfig.class);
        JdbcTemplate template=context.getBean(JdbcTemplate.class);
        String sql="Select bid,title,author,price from book";
        List<Book> bookList=template.query(sql,new BookRowMapper());
        for(Book book : bookList) {
            LOGGER.info("查询到的book信息为：{}", book);
        }
    }
    /**
     * 分页查询
     * */
    private static void querySplitTest(){
        int currentPage=1;
        int lineSize=3;
        ApplicationContext context=openSpring(HikariCPDataSourceConfig.class);
        JdbcTemplate template=context.getBean(JdbcTemplate.class);
        String sql="Select bid,title,author,price from book limit ?,?";
        List<Book> bookList=template.query(sql,new BookRowMapper(),(currentPage-1)*lineSize,lineSize);
        for(Book book : bookList) {
            LOGGER.info("查询到的book信息为：{}", book);
        }
    }
    /**
     * 获取数据总条数
     * */
    private static void queryCountTest(){
        ApplicationContext context=openSpring(HikariCPDataSourceConfig.class);
        JdbcTemplate template=context.getBean(JdbcTemplate.class);
        String sql="Select count(*) from book where title like ?";
       long count=template.queryForObject(sql,Long.class,"%Spring%");

            LOGGER.info("查询到的数据总条数：{}", count);

    }

    private static void transactionTest(){
        ApplicationContext context=openSpring(HikariCPDataSourceConfig.class);
        JdbcTemplate template=context.getBean(JdbcTemplate.class);
        PlatformTransactionManager transactionManager=context.getBean(PlatformTransactionManager.class);
        TransactionStatus status=transactionManager.getTransaction(new DefaultTransactionDefinition());//以默认的事务传播、隔离级别开启事务
        String sql="insert into book (title,author,price)Values(?,?,?)";

        try{
            template.update(sql,"数据库原理","jjX",50.2);
            template.update(sql,"高级程序设计语言C",null,56.6);//会出现异常
            transactionManager.commit(status);//未出现异常，则正常提交事务
            LOGGER.info("新增数据成功");
        }catch(Exception e){
            transactionManager.rollback(status);
            LOGGER.error("新增异常：{}",e.getMessage());
        }
    }
    /**
     * 测试Spring的隔离级别
     *  由测试可知，使用默认隔离级别的时候，事务A两次读取的结果都是一样
     *  由于添加了延迟，所以在两次读取之间，事务B是进行了数据的修改的，由此可见
     *  默认隔离级别为可重复读
     *
     * */
    private static void isolationTest() throws Exception{
        Integer bid=4;
        String querySql="Select bid,title,author,price from book where bid=?";
        String updateSql="update book set title=? where bid=?";
        ApplicationContext context=openSpring(HikariCPDataSourceConfig.class);
        JdbcTemplate template=context.getBean(JdbcTemplate.class);
        PlatformTransactionManager transactionManager=context.getBean(PlatformTransactionManager.class);
        //修改事务默认的隔离级别
        DefaultTransactionDefinition definition=new DefaultTransactionDefinition();
        definition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);//设置隔离级别为读提交

        new Thread(()->{
            //TransactionStatus statusA=transactionManager.getTransaction(new DefaultTransactionDefinition());//开启事务
            TransactionStatus statusA=transactionManager.getTransaction(definition);
            Book book=template.queryForObject(querySql,new BookRowMapper(),bid);
            LOGGER.info("【{}】第一次读取，结果为：{}",Thread.currentThread().getName(),book);
            try{
                TimeUnit.SECONDS.sleep(5);//延迟5s，等待线程B修改数据
            }catch(Exception e){
                LOGGER.error("出现异常：{}",e.getMessage());
            }
            Book book1=template.queryForObject(querySql,new BookRowMapper(),bid);
            LOGGER.info("【{}】第二次读取，结果为：{}",Thread.currentThread().getName(),book1);

        },"事务处理-A").start();

        new Thread(()->{
            //TransactionStatus statusB=transactionManager.getTransaction(new DefaultTransactionDefinition());
            TransactionStatus statusB=transactionManager.getTransaction(new DefaultTransactionDefinition());
            Book book=template.queryForObject(querySql,new BookRowMapper(),bid);
            LOGGER.info("【{}】第一次读取，结果为：{}",Thread.currentThread().getName(),book);
            try{
                TimeUnit.MILLISECONDS.sleep(500);//延迟500ms
                int result=template.update(updateSql,"SpringBoot",bid);
                LOGGER.info("【{}】数据修改成功，影响条数={}",Thread.currentThread().getName(),result);
                transactionManager.commit(statusB);
            }catch(Exception e){
                LOGGER.error("数据更新出现异常：{}",e.getMessage());
                transactionManager.rollback(statusB);
            }
        },"事务处理-B").start();
        TimeUnit.SECONDS.sleep(20);//延迟处理
    }

    private static void TransactionalTest(){
        ApplicationContext context=new ClassPathXmlApplicationContext("classpath:spring/application.xml");
        BookService bookService=context.getBean(BookService.class);
        bookService.remove(9);
    }

    private static void aopTransactionTest()throws Exception{
        ApplicationContext context=new ClassPathXmlApplicationContext("classpath:spring/application.xml");
        IBookService bookService=context.getBean(IBookService.class);
        Book book=new Book();
        book.setTitle("SpringCloud开发实战");
        book.setAuthor("xiajj");
        book.setPrice(77.77);
        book.setBid(13L);
        //bookService.addBook(book,false);
        bookService.editBook(book,true);
    }

    private static void aopBeanTransactionTest()throws Exception{
        ApplicationContext context=openSpring(GlobalConfig.class);
        IBookService bookService=context.getBean(IBookService.class);
        Book book=new Book();
        book.setTitle("SpringCloud开发实战");
        book.setAuthor("xiajj");
        book.setPrice(77.77);
        book.setBid(13L);
        //bookService.addBook(book,false);
        bookService.editBook(book,true);
    }
}
/**
 * 定义RowMapper接口的实现类，在查询的时候使用
 * 可以将返回的ResultSet转为Book对象
 * */
class BookRowMapper implements RowMapper<Book>{

    @Override
    public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
        Book book=new Book();
        book.setBid(rs.getLong(1));
        book.setTitle(rs.getString(2));
        book.setAuthor(rs.getString(3));
        book.setPrice(rs.getDouble(4));
        return book;
    }


}