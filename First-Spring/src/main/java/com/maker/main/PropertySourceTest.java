package com.maker.main;

import com.maker.config.MessageConfig;
import com.maker.vo.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.*;

import java.util.Map;
import java.util.Properties;

/**
 * PropertySource(抽象类)
 *  属性源（key-value集合）
 *  在实际开发之中，有些配置项是通过*.properties进行注入的，有些配置项是程序解析所得的map
 *  应用程序在获取这些属性的时候，需要根据不同的数据源使用不同的方法来获取对应的数据值，对于外部应用的编写
 *  就会变得比较繁琐（map为get(),properties为getProperty()），那么需要找到一个过渡点.
 *  正式因为有操作统一的需求，所以Spring当中提供了PropertySource的实例
 *
 * 使用PropertySource可以实现单种资源的管理，但是在程序的开发中，肯定会有多种资源的存在，所有就要
 * 对资源进行进一步的抽象处理----PropertySources（接口），所有的PropertySource对象向此接口注册后，就可以
 * 根据指定的名称获取一个PropertySource的对象
 *  MutablePropertySources
 *      Spring内置的PropertySources实现子类
 *
 * PropertyResolver属性解析
 *  通过PropertySources的资源管理，如果要想获取属性内容，先得获取属性源，然后才能获取到相应的属性
 *  这样的操作，是在是太啰嗦了。Spring是一个容器，它的内部必定有很多属性源，很多使用者可能并不知道
 *  这些属性源的分类，所以就要有一个统一的属性解析处理操作
 *      PropertyResolver接口
 *
 *
 * ConfigurableEnvironment
 *      Spring容器在运行时并没有脱离掉原始的JVM，那么这个时候往往会读取一些系统的属性信息，
 *      而这些信息会统一保存在PropertySource实例中
 *      即在Spring中，若想获取JVM的系统属性可以通过ConfigurableEnvironment接口来进行获得
 *      不需要在依赖System.getProperty()和System.getenv()
 *
 *  Environment和Profile
 *      通过Spring中的Environment来进行Profile环境的切换和设置
 *      1.在vo包下创建一个Message Bean对象在，并在config包下创建Bean的注册类
 *      2.在environmentProfileTest()方法中，通过MessageConfig配置类，获取相应的context，然后获取Bean实例
 *
 *
 *  ConversionService数据转换服务
 *      之前进行Bean分析的时候，注册Bean时，所有注入的属性都会从字符串转到相应的类型，例如Integer、Double、Boolean等等
 *      该接口的实例，可以通过ConfigurablePropertyResolver中的getConversionService()方法来进行获取
 *      通过相应的ConfigurablePropertyResolver实现子类，便可获取到ConversionService
 *      一般获取到的是DefaultConversionService,此类继承了GenericConversionService类，该类实现了ConversionService的子接口ConfigurableConversionService
 *      ConfigurableConversionService同时继承了ConverterRegistry接口，真正的转换类型的核心则是Convert<?,?>接口
 *
 *
 *
 *
 *
 *
 *
 * */
public class PropertySourceTest {
    private final static Logger LOGGER= LoggerFactory.getLogger(PropertySourceTest.class);
    public static void main(String[] args) {
        //mapProertySourceTest();
        //PropertySourcesTest();
        //resolverTest();
        //environmentProfileTest();
        conversionServiceTest();



    }

    private static void mapProertySourceTest(){
        Map<String,Object> map= Map.of("xia","夏","jun","俊","jie","杰");//java层面的集合管理
        PropertySource source=new MapPropertySource("name",map);//资源管理
        //将key-value结构交给PropertySource进行管理，这样获取所需要的value时的方法便统一了
        LOGGER.info("【xia】={}",source.getProperty("xia"));
    }

    private static void PropertySourcesTest(){
        Map<String,Object> map= Map.of("xia","夏","jun","俊","jie","杰");
        PropertySource mapSource=new MapPropertySource("name",map);//资源管理
        Properties prop=new Properties();
        prop.setProperty("java","77.77");
        prop.setProperty("SSM","88.88");
        prop.setProperty("SpringBoot+SpringCloud","101.11");
        PropertySource bookSource=new PropertiesPropertySource("book",prop);
        MutablePropertySources sources=new MutablePropertySources();
        sources.addLast(mapSource);//管理属性源
        sources.addLast(bookSource);
        LOGGER.info("【xia】={}",sources.get("name").getProperty("xia"));
        LOGGER.info("【java】={}",sources.get("book").getProperty("java"));

    }

    private static void resolverTest(){
        Map<String,Object> map= Map.of("xia","夏","jun","俊","jie","杰");
        PropertySource mapSource=new MapPropertySource("name",map);//资源管理
        Properties prop=new Properties();
        prop.setProperty("java","77.77");
        prop.setProperty("SSM","88.88");
        prop.setProperty("SpringBoot+SpringCloud","101.11");
        PropertySource bookSource=new PropertiesPropertySource("book",prop);
        MutablePropertySources sources=new MutablePropertySources();
        sources.addLast(mapSource);//管理属性源
        sources.addLast(bookSource);
        PropertySourcesPropertyResolver resolver=new PropertySourcesPropertyResolver(sources);
        LOGGER.info("【xia】={}",resolver.getProperty("xia"));
        LOGGER.info("【java】={}",resolver.getProperty("java"));
        LOGGER.info("【SSM】={}",resolver.resolvePlaceholders("${SSM}"));//解析相应的字符串，查找所需要的值
        //解析后如果查找不到相应的值，会直接返回字符串
        LOGGER.info("【jayjxia】={}",resolver.resolvePlaceholders("${jayjxia}"));
        //如果不加上前缀后缀${}，直接返回字符串的内容
        LOGGER.info("【jj】={}",resolver.resolvePlaceholders("jj"));
        LOGGER.info("【jun】={}",resolver.resolvePlaceholders("jun"));
    }

    private static void environmentProfileTest(){
        AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext();//注解方式启动Spring容器
        context.getEnvironment().setActiveProfiles("test");//获取到Spring保存的环境后，设置当前的Profile
        context.register(MessageConfig.class);
        context.refresh();
        Message msg=context.getBean(Message.class);
        LOGGER.info("{}",msg);
    }

    private static void conversionServiceTest(){
        ConfigurableEnvironment environment=new StandardEnvironment();//ConfigurableEnvironment接口集成了ConfigurablePropertyResolver
        ConfigurableConversionService conversionService=environment.getConversionService();
        LOGGER.warn("ConversionService实现类{}",environment.getConversionService().getClass());
        Double result=conversionService.convert("2.5",Double.class);//转型
        LOGGER.info("转型后计算结果为{}",result*4);

    }
}
