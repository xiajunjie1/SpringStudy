package com.maker.main;

import com.maker.aware.GetDatabaseConfig;
import com.maker.aware.IDatabaseConfigAware;
import com.maker.config.DeptConfig;
import com.maker.config.MessageChannel;
import com.maker.config.MessageChannelConfig;
import com.maker.dao.DeptDao;
import com.maker.service.IMessageService;
import com.maker.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.crypto.dsig.XMLValidateContext;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

/**
 * Spring
 *  Spring Test启动
 *  在Spring内部提供了一个SpringTest依赖库的支持
 *  需要整合Junit5到Spring中，在dependencies.gradle中定义相关依赖库
 *
 *
 *  Spring整合Logback日志组件
 *      1.在项目中添加slf4j和logback依赖
 *      2.要想使用logback，需要创建一个相应的xml配置文件，该配置文件保存resource目录中，名称为logback.xml
 *
 *  Spring依赖注入
 *      通过XML配置文件进行依赖注入
 *          1.定义一个MessageService的接口实现类，此类中包含一个MessageConfig的属性
 *          2.定义一个MessageConfig的设置类，此类中包含若干个属性
 *          3.在XML文件中配置两个类，并且进行属性的注入
 *          进行bean配置的时候，要设置属性内容则使用property标签
 *              value：直接定义注入的值，这个值的类型是字符串，但是它可以根据目标属性的类型来自动转型
 *              ref：表示对指定对象Bean的引用（已经配置过Bean），定义了不同Bean之间的关联结构
 *
 *      通过p命名空间进行依赖注入
 *          如果按照上述方式进行Bean对象的定义，则会造成一个问题：XML配置文件过大
 *          采用p命名空间进行配置
 *          1.修改application.xml文件的命名空间，添加：xmlns:p="http://www.springframework.org/schema/p"
 *          2.修改bean对象的定义
 *      构造方法注入
 *          在Spring中，并没有脱离原生的Java的特点，依然需要默认通过无参构造进行对象实例化
 *          而后基于反射配置实现setter方法的调用
 *          但是如果相应的Bean对象没有无参构造方法，要如何执行呢
 *          创建一个MessageConfig_copy类，此类中创建一个三参构造，在application.xml中进行bean对象的实例化
 *
 *  Spring自动装配
 *      自动装配属于依赖注入中的一种简化形式
 *      将<property name="xxx" ref="xxx" />给去掉
 *      直接在bean标签上加上属性 autowire="byType/byName"
 *      byType：根据属性的类型自动注入对象（此对象必须在Spring中创建）
 *      byName：根据名称实现自动注入对象（名称即在Spring创建对象的名称和属性名称的匹配）
 *
 *      若Spring中定义了多个同种类型的Bean对象，并且没有进行名称的标识，但是又希望自动注入此对象，那么可以采取以下做法
 *          1.在其中一个bean对象定义上，加上属性primary="true"，这样此bean就会作为优先注入的bean对象
 *          2.在不需要注入的bean对象定义上，加上属性autowire-candidate="false"属性，代表将此bean类排除候选对象
 *
 *  Prototype原型设计模式
 *      当在Spring在定义bean对象的时候，如果scope使用的是prototype，那么它便会生成出来不同的对象，而不是使用单例设计模式创建bean对象
 *      prototype实际上并不是每一次都产生一个新对象，并不是直接使用关键字new来进行定义，而是说此时试用了Java中的原型设计模式（prototype）
 *          A.Spring程序之中的Bean管理，并不完全按照Java对象实例化的方式处理的
 *          B.Spring之中的初始化实际上会将配置文件之中的属性全部进行设置，而这一操作在整个Spring中仅仅为对象实例化处理；
 *          C.Spring中会存在大量的Bean的配置步骤（Bean的声明周期）
 *          此时可以通过ApplicationContext之中获取到的Bean实例，是一个已经通过Spring完全初始化的Bean对象，所以这个操作
 *          已经离所谓的new步骤过去很多步了，所谓的原型设计可以简单的理解为一种克隆操作，通过一个已经存在的Bean对象克隆若干个
 *          新的对象的产生。
 *
 *       Java之中关于对象克隆的处理
 *          对象克隆处理分为浅克隆和深克隆两种机制，Object类之中提供的clone()方法就属于浅克隆
 *          Spring中的prototype模式使用的是深克隆的处理形式
 *
 *       Spring集合注入
 *          在di包下创建collections.AllConfigs类
 *          List/Array：在Spring中，List用到的实现类是ArrayList实现类，所以在Spring中List和数组是等价的
 *          Set：在Spring中，Set用到的实现类为LinkedHashSet（有序，无重复Set）类
 *          Map：Spring中，Map注入用到的实现子类为LinkedHashMap（实现顺序式的Map存储）
 *          以上集合都实现了顺序存储，因为在Spring中bean的存储是固定的
 *
 *          Properties注入：此类属于Hashtable的子类
 *          1.在config包下定义MessageProperties类（后期会用properties文件代替）保存信息
 *
 *  Spring Annotation
 *      在Spring中想要使用注解，首先需要在配置文件中，添加context的命名空间xmlns:context="http://www.springframework.org/schema/context"
 *      而后需要在context配置项中添加扫描包，可以是具体的包名或者是父包名
 *      开启注解配置项：<context:annotation-config />
 *      配置扫描包： <context:component-scan base-package="com.maker" />
 *
 *      Spring注解中，最为核心的注解为：@Component,由其衍生出来以下三个注解
 *          @Service :业务层注解
 *          @Controller：控制层注解
 *          @Repository：数据层注解
 *      观察以上三个注解的源码可知，它们都引用了@Component注解，并且都包含有：@AliasFor(annotation = Component.class)
 *      使用注解在进行Bean注册的时候，有两种方式取设置Bean的名称，一种是默认名称（如MessageConfig类），它的默认注册名称为（messageConfig）
 *      另一种是使用注解的Value属性来进行名称的设置
 *
 *      @Configuration注解：代表配置注册Bean
 *          该注解标在类上，代表此类为配置类，类中方法配合@Bean注解使用，表示Bean注册
 *          若不指定名称，那么默认方法名称则是Bean的注册名称，并且在测类中，如果引用相应的Bean
 *          对象作为方法参数，那么都是根据Bean名称进行自动匹配的
 *          可以使用该方式，注册不在扫描包范围下的bean对象
 *
 *      @Qualifier注解：指定配置类的名称
 *          如果注册了多个同类型的bean，需要自动注入的属性名称与其中的Bean名称相同的话可以注入，
 *          但是若属性名称与Bean名称不同，那么就无法自动注入，这个时候需要用到Qualifier注解
 *          还有一种方式是通过将@Primary注解添加到配置类的相应方法上，这样在自动注入时就会优先注入此方法返回的Bean对象
 *
 *     @DependsOn注解
 *          如果现在使用配置类进行配置bean对象，那么配置的顺序是按照代码的定义的顺序执行的
 *          但是如果不同的Bean配置在了不同的配置类之中，那么顺序又该如何配置呢
 *          在相应的方法上加上@DependsOn("xxxx")，表示在xxxx执行后才执行此方法注册Bean
 *
 *      @Conditial注解
 *          用在注册Bean使用，某些Bean在满足一定条件后才能进行注册，这时候就要通过Conditional接口定义条件逻辑
 *          需要创建一个Condition接口的实现子类，重写matches方法，当返回false代表不注册
 *          当返回true代表可以注册，并且在对象的方法上加上@Conditional({xxxx.class})，此处的xxxx为Condition接口的实现子类
 *      @Profile注解
 *          在Spring中如果谈到了profile，本质上指的是多个不同的运行环境设置问题
 *          不同的环境可以使用不同的Bean定义
 *          在config包下创建profile包，在profile包中分别定义不同环境的配置类
 *          在每个配置类上都加上@Profile用来声明其环境
 *          切换不同环境来执行不同的配置类
 *              1.在Test测试用例中，使用@ActiveProfiles注解指定当前的环境
 *              2.在IDEA中设置一些JVM的初始化参数
 *                  Run-》Edit Configurations-》ALT+M-》add VM Options-》添加-Dspring.profiles.active=test/dev/product...
 *                  注意：此方式在测试用例当中无法生效,在main下创建一个ProfileTest进行测试
 *
 *      @ComponentScan包扫描注解
 *          可以通过此注解实现包扫描的功能，可以用于配置在全局配置类上
 *          @ComponentScan({xxxx,xxxx,xxx...})
 *          @ComponentScans也可以配置多个扫描包，但是它的用法：@ComponentScans({@ComponentScan({xxx,xxxx...}),
 *              @ComponentScan({xxx,xxxx...})})，相比较起来，@ComponentScan要简单很多
 *
 *      Resource接口
 *          描述资源的统一管理，在实际的开发中，会有各种各样的资源需要进行读取，例如：文件资源
 *          CLASSPATH资源，网络资源等等，那么为了规范化这些资源的读取，Spring就提供了Resource接口
 *          该接口还有一个继承的父接口：InputStreamSource
 *          创建ResourceTest类，进行各种Resource子类的使用测试
 *
 *
 *  Spring表达式的基本使用
 *      创建SpELTest类进行Spring表达式的使用
 *
 *
 *  ApplicationContext的继承结构
 *      在Spring容器启动的时候，目前接触到了两个操作类：
 *      ClassPathXmlApplicationContex(xml配置文件)、AnnotationConfigApplicationContext(基于注解进行启动)
 *      public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory,
 * 		        MessageSource, ApplicationEventPublisher, ResourcePatternResolver
 *
 *
 *   ResourcePatternResolver：资源匹配解析
 *      它继承了ResourceLoader，由此可知，所有的ApplicationContext的接口子类实例都可以向ResourceLoader接口进行转换
 *
 *   EnvironmentCapable：实现资源的管理控制
 *
 *   ApplicationEventPublisher：Spring事件发布处理接口
 *      自定义事件和事件监听的使用
 *      1.创建一个MyEvent事件类
 *      2.定义一个MyEventListener事件监听类
 *      3.创建一个配置类，注册监听器
 *      4.创建一个Spring启动类，用Annotation启动，并发布事件
 *
 *
 *  MessageSource接口
 *      程序开发与设计中，肯定要考虑到国际化资源的读取操作，所以在Spring的内部
 *      就提供了一个MessageSource公共操作接口，所有的资源可以通过该接口提供的方法
 *      根据key来获取
 *      1.在resources下创建i18n/Message*.properties的资源文件
 *      2.编写相应的代码，获取Message*.properties中的name属性
 *
 *
 *  Bean的初始化和销毁
 *      在Java中，对象的初始化和销毁都需要按照特定的结构来实现，但是Spring中对于初始化和销毁是提供了
 *      自定义的方法的支持，也就是说这些方法可以直接通过配置文件来进行定义。
 *      1.XML配置文件方式定义初始化方法和销毁方法
 *          在vo包下创建一个Bean对象
 *          在xml配置文件中进行Bean对象的初始化，同时指定初始化方法和销毁方法
 *      2.使用接口方式定义初始化方法和销毁方法
 *          在vo包下创建一个Bean对象，并且让此Bean对象实现InitializingBean和DisposableBean两个接口
 *          覆写两个接口中的方法
 *
 *      3、JSR-250，通过注解的方式配置初始化和销毁方法
 *          引入依赖：javax.annotation:javax.annotation-api:1.3.2，JDK1.8中自带此包,JDK1.9之后被去掉
 *          在vo包中编写Bean对象，编写初始化方法和销毁方法，并加上相应的注解
 *
 *
 *  Lifecycle生命周期处理规范
 *      public interface Lifecycle {
 *          void start();//生命周期的开始
 *          void stop();//生命周期的结束
 *          boolean isRunning();//是否正在运行
 *      }
 *      在vo包中创建MyBean4的类
 *
 *
 *  SmartLifecycle
 *      在实际的项目中，可能会有若干个不同的Bean都需要生命周期的扩展处理
 *      为了更好的编排多个Bean的生命周期，Spring提供了SmartLifecycle子接口，
 *      并提供了生命周期的新控制方法
 *          此接口当中有一个getPhase方法，此方法返回的值越小，则该Bean的初始化越早，销毁顺序越晚
 *
 *  BeanDefinitionReader
 *      Spring中考虑到满足自身要求的XML文件读取机制，提供了一套自己的XML解析处理标准，
 *      同时该标准兼容JDK提供的DOM和SAX解析处理模型。在Spring提供的操作标准中最为重要的
 *      是BeanDefinitionReader接口。
 *      Spring中通过XML启动容器，核心的机制就在于当前的接口，通过此接口去分析与之相关的其他接口，
 *      并且结合DOM/SAX解析机制就可以实现XML配置项的读取了
 *
 *  XmlBeanDefinitionReader
 *      此类为BeanDefinitionReader的实现子类\
 *      可以通过此类加上xml文件，获取到Spring中所有定义注册的Bean的个数
 *      使用方式参见：xmlBeanDefinitionReaderTest()
 *
 *  ResourceEntityResolver
 *      Spring中XML配置文件结构之中，可以通过Schema（XSD的模式）来进行程序文件的结构定义，
 *      而这个结构定义上分为两个部分，一个是pulicId，一个是systemId。例如：
 *          publicId:http://www.springframework.org/schema/context
 *          systemId:https://www.springframework.org/schema/context/spring-context-4.3.xsd
 *       Spring中提供了一个EntityResolver接口，用来获取指定的XSD的描述规范，返回的对象为InputSource实例
 *
 *
 *  BeanDefinition
 *      在Spring中如果要进行反射处理，它会分为三个部分
 *      1.进行数据的解析操作
 *      2.将所有解析的结果以BeanDefinition形式保存在Spring指定的集合中
 *      3.进行对象的实例化，通过BeanDefinition接口完成
 *
 *      所有关于Bean的信息都保存在BeanDefinition实例接口当中，所有通过XML配置的
 *      Bean，或者是注解的方式配置的Bean，最终解析完成的信息都在此接口的实例中存储，
 *      在进行对象实例化时，是通过了BeanDefinition接口获取了Bean名称，实现了最终反射
 *      实例化。
 *
 *
 *  BeanDefinitionParserDelegate
 *      Spring针对自己的XML文件的解析提供了很多的新的接口，以及新的处理操作类。
 *      但是最终如何进行解析处理就需要通过BeanDefinitionParserDelegate进行处理
 *      基于XML配置的Spring环境，在上下文启动时，往往都需要定义配置资源路径，这样应用程序
 *      才可以对指定的资源数据进行解析处理，所有整个解析流程分为两个部分：获取XML文档，另一个就是
 *      进行文档的解析。
 *      传统的DOM操作实在是太繁琐了，所以Spring提供了一个DocumentLoader处理接口，该接口
 *      可以根据指定XML数据、EntityResolver创建Document接口实例，从而便于后续解析操作。
 *      应用程序获取到Document接口后，就需要进行进一步的解析，由于整个XML配置文件是以“<bean>”元素的定义为主
 *      所以Spring提供了BeanDefinitionParserDelegate工具类,该类提供了一个parseBeanDefinitionElement()方法，
 *      使用此方法可以将每一个XML文件指定的Element资源实例转为BeanDefinition接口实例，从而为下一步的Bean实例化做出准备
 *
 *
 *
 *  BeanFactory
 *      在Spring中Bean不管使用XML定义还是注解定义，都是通过BeanFactory实现处理的，它就是一个Bean的创建工厂
 *      在实际的Spring容器启动过程之中，BeanDefinition接口实例也需要被BeanFactory所管理
 *      AnnotationConfigApplicationContext和ClassPathXmlApplicationContext还有AbstractApplicationContext都实现了该接口
 *      ApplicationContext接口也是其子接口
 *
 *  ListableBeanFactory
 *      在一些开发场景当中，除了Bean本身的需求之外，也需要一些额外的配置信息
 *      例如：同一类型Bean存在的数量，容器中包含的Bean的数量、使用指定的注解Bean的信息等
 *
 *
 *  ConfigurableBeanFactory
 *      所有的Bean实例都可以通过BeanFactory接口定义的getBean()方法获取
 *      而为了更加规范化的管理Bean模式，Spring提供了一个ConfigurableBeanFactory接口
 *      该接口中提供了一个getSingleton方法，此方法只能获取单例模式Bean对象
 *
 *
 *  Bean的创建
 *      AutowireCapableBeanFactory接口
 *      Bean注册的功能由该子接口完成
 *      <T> T createBean(Class<T> beanClass) throws BeansException; 根据Bean类型创建Bean对象
 *
 *      Spring三级缓存：
 *          所有被Spring生成的Bean实例，最终都是保存在Spring上下文容器之中，而具体存放的位置是在DefaultSingletonBeanRegistry类属性之中
 *          在该类中，提供有三个核心属性：
 *              private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
                private final Map<String, ObjectFactory<?>> singletonFactories=new HashMap<>(16);
                private final Map<String, Object> earlySingletonObjects=new ConcurrentHashMap<>(16);
 *          为了可以方便的获取到Spring之中所有的Bean对象，开发者肯定要在Spring上下文中通过一个集合保存所有的Bean信息
 *          这些Bean信息保存。Spring中就提供了三个集合
 *          singletonObjects：一级缓存，保存所有已经完全初始化的Bean对象
 *          earlySingletonObjects：二级缓存，保存已经实例化完成，但是未初始化成功的Bean，用于解决在整个项目中的循环依赖的问题
 *          singletonFactories：三级缓存，保存缓存工厂，用于解决循环依赖的问题
 *
 *  ObjectProvider
 *      当同一个类型有多个Bean的时候，就可以通过ObjectProvider接收所以的Bean实例对象
 *      然后在通过Stream()选择其中的一个Bean实例进行注入。
 *
 *
 *  FactoryBean
 *      BeanFactory可以通过加上前缀"&"的方式，获取到FactoryBean
 *      当Spring中，需要保存某一个类型的多个不同对象的时候，可以使用到FactoryBean
 *      来实现。
 *
 *
 *  BeanFactoryPostProcessor
 *      当Spring中，Bean对象的初始化完成后，如果在Spring中有注册此接口的实现子类
 *      那么在Spring初始化完成后，会紧接着调用此实现子类中的方法。可以在此方法中，
 *      修改某些Bean对象的属性值，或者进行某些类的配置处理。
 *
 *      该接口实现子类中的方法，是通过PostProcessorRegistrationDelegate进行调用的
 *      而调用的时候，则是Spring容器对象再进行refresh的时候进行的。
 *
 *
 *  EventListenerMethodProcessor
 *      自定义事件处理类，此类为BeanFactoryPostProcessor的实现子类
 *      所有Spring的事件处理，都是通过该类，在Spring初始化的时候进行注册的
 *
 *  PropertySourcesPlaceholderConfigurer
 *      在Spring中可以通过@Value注入配置文件的属性，这个操作，按正常的道理来讲
 *      是在Bean实例化完成之后才可以进行属性内容的注入。如果现在一个Bean之中属性已经配置完成了
 *      如果想要通过资源进行属性内容的设置，一定要使用到特定的处理逻辑完成开发操作，自然这个这个操作
 *      需要在BeanFactoryPostProcessor的相关子类中完成。
 *      此类负责将目标的配置文件解析并生成KV键值对
 *
 *  ConfigurationClassPostProcessor
 *      在Spring配置之中，可以通过@Configuration注解进行配置类的定义，同时在配置类里面
 *      还可以定义有若干跟Bean对象实例信息，但是这个注解是如何解析的呢？
 *      问题：请问使用了@Configuration注解的配置，是否会在Spring当中进行Bean注册呢
 *          所有使用@Configuration的配置类，也会在扫描的时候自动成为一个Bean，并且注册到Spring之中
 *      @Configuration配置类的Bean注册，实际上是通过BeanFactoryPostProcessor的子类：ConfigurationClassPostProcessor
 *      内部通过一系列的调用，对项目之中出现的@Component、@Bean、@PropertySource等注解进行了解析处理以及相关的内容配置
 *
 *
 *  BeanPostProcessor处理
 *      default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
 * 		        return bean;
 *          }//在Bean初始化之前执行
 *
 *      default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
 * 		        return bean;
 *        }//在Bean初始化之后执行
 *      注意，当此接口的实现子类和BeanFactoryPostProcessor同时使用时，BeanFactoryPostProcessor的实现子类
 *      中获取到的Bean对象，在BeanPostProcessor子类中将不再被获取。因为被BeanFactoryPostProcessor接口子类
 *      获取到的Bean，表示该Bean已经初始化完成
 *
 *
 *  Bean初始化流程处理
 *      Spring中对象的实例化是通过反射机制实现的，而这个反射机制在调用的时候主要采用到了
 *      BeanUtils工具类所提供的方法完成的，只不过中间会经历一系列的解析过程。实际上最为麻烦
 *      的地方并不在于实例化，而在于Spring中提供了另一套初始化流程
 *      创建一个lifecycle包，并创建一个Bean类，在创建一个BeanFactoryPostProcessor实现类和BeanPostProcessor
 *
 *  Aware依赖管理操作
 *      Aware接口内部并没有任何的方法，那么它就是一个标记接口，它就相当于Spring内部的一个结构
 *      通过这个接口的定义而后可以完成某些特定功能。
 *      创建aware包
 *      在aware包下创建一个IDatabaseConfigAware接口（继承标记接口Aware），用来完成DatabaseConfig对象的自动注入
 *      创建IDatabaseConfigAware的实现类，和一个DatabaseConfigPostProcessor实现自动注入DatabaseConfig对象
 *
 *
 *  Spring配置文件路径处理
 *      Spring是如何解析XML资源的：
 *          ClassPathXmlApplicationContext
 *              调用构造方法，内部会执行：this(configLocations, true, parent);
 *              点开this()的构造方法：
 *              	super(parent);
 * 		            setConfigLocations(configLocations);
 * 		            if (refresh) {
 * 			                        refresh();
 *                                }
 *               refresh传入的是一个true，所以在使用xml配置模式时，不需要我们手动执行refresh()方法
 *               但是无论是注解方式启动还是使用xml方式启动，都需要执行refresh()方法进行刷新
 *               setConfigLocations(configLocations)方法是具体负责解析XML的方法
 *
 *          FileSystemXmlApplicationContext
 *       它们都继承了AbstractXmlApplicationContext类
 *
 *  Spring刷新上下文
 *      Spring容器启动中最为核心的部分，就是refresh()方法了
 *      public void refresh() throws BeansException, IllegalStateException {
 *    synchronized (this.startupShutdownMonitor) {  // 刷新的时候进行同步处理
 *       // 启动步骤的记录，因为不同的操作会有不同的启动步骤，这里面主要做一个标记
 *       StartupStep contextRefresh = this.applicationStartup.start("spring.context.refresh");
 *       // Prepare this context for refreshing.
 *       prepareRefresh();             // 准备执行刷新处理
 *       // Tell the subclass to refresh the internal bean factory.
 *       ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();// 获取Bean工厂
 *       // Prepare the bean factory for use in this context.
 *       prepareBeanFactory(beanFactory); // BeanFactory初始化，BeanFactory初始化完了
 *       try {
 *          // Allows post-processing of the bean factory in context subclasses.
 *          postProcessBeanFactory(beanFactory); // BeanFactoryPostProcessor处理接口
 *          StartupStep beanPostProcess =
 *          this.applicationStartup.start("spring.context.beans.post-process"); // 步骤记录
 *          // Invoke factory processors registered as beans in the context.
 *          invokeBeanFactoryPostProcessors(beanFactory); // 执行BeanFactoryPostProcessor子类
 *          // Register bean processors that intercept bean creation.
 *          registerBeanPostProcessors(beanFactory); // 注册BeanPostProcessor
 *          beanPostProcess.end();// Bean初始化完成
 *          // Initialize message source for this context.
 *          initMessageSource();// 初始化MessageSource接口实例（资源加载）
 *          // Initialize event multicaster for this context.
 *          initApplicationEventMulticaster();// 初始化事件广播
 *          // Initialize other special beans in specific context subclasses.
 *          onRefresh();// 初始化其他特殊的操作类，方法是空的
 *          // Check for listener beans and register them.
 *          registerListeners();// 注册监听
 *          // Instantiate all remaining (non-lazy-init) singletons.
 *          finishBeanFactoryInitialization(beanFactory); // 实例化剩余的对象（非Lazy）
 *          // Last step: publish corresponding event.
 *          finishRefresh();// 相关事件发布
 *       } catch (BeansException ex) {
 *          if (logger.isWarnEnabled()) { // 日志记录
 *             logger.warn("Exception encountered during context initialization - " +
 *                   "cancelling refresh attempt: " + ex);
 *          }
 *          // Destroy already created singletons to avoid dangling resources.
 *          destroyBeans();// 销毁全部的bean对象
 *          // Reset 'active' flag.
 *          cancelRefresh(ex); // 设置一个存活的状态标记
 *          // Propagate exception to caller.
 *          throw ex; // 进行异常的抛出
 *       } finally { // 不管是否有异常都执行
 *          // Reset common introspection caches in Spring's core, since we
 *          // might not ever need metadata for singleton beans anymore...
 *          resetCommonCaches();// 重置缓冲
 *          contextRefresh.end();// 步骤的结束
 *       }
 *    }
 * }
 *
 *  StartupStep
 *      在Spring refresh()的源码当中有如下一段代码：
 *      StartupStep contextRefresh = this.applicationStartup.start("spring.context.refresh");
 *      此时是通过StartupStep实现了一个启动步骤的记录，
 *
 *  prepareRefresh()
 *      刷新预处理操作，该方法主要是进行刷新之前预处理，如记录开始时间戳，将激活标记（active）设为true，
 *      将关闭标记（closed）设为false，初始化属性源集合（PropertySources），
 *      根据当前的环境进行一些必要属性的验证处理，保存事件监听，准备进行事件发布。
 *
 *  obtainFreshBeanFactory()
 *      进行BeanFactory对象的刷新
 *      返回BeanFactory对象
 *      1.关闭当前已有的BeanFactory的配置（就是将ApplicationContext子类中的BeanFactory都置为空）
 *      2.重新通过XML配置文件进行BeanDefinition的加载，BeanFactory内部的三级缓存就全部进行了更新
 *      3.返回已经重新初始化完成的BeanFactory对象。
 *
 *  prepareBeanFactory(beanFactory)
 *      预处理BeanFactory
 *      1.配置SpEL表达式解析处理器，可以在Spring容器中使用SpEL进行处理了
 *      2.添加资源属性编辑注册器的定义，以实现资源的注入处理
 *      3.添加了一系列的Aware的后置处理，实现各类核心资源的注入
 *      4.配置Spring容器进行依赖管理时所需要的相关的类：BeanFactory、ResourceLoader、ApplicationContext、事件。
 *      5.处理项目中可能使用到的LTW技术（外部jar包的织入处理）
 *          LTW:在代理模式的织入操作实现中，一般会有三类的方式
 *              1.编译器织入（由Maven或者Gradle等工具在项目构建的时候自动织入一些代码以完成代理）
 *              2.类加载期织入（LTW处理模式，使用“-javaagent:xxx.jar"命令进行处理）
 *              3.运行期织入处理（JDK动态代理，CGLib工具包）
 *      6.处理一系列的环境配置项。
 *
 *  initMessageSource()
 *      MessageSource接口可以实现所有资源配置文件的加载，而后所有的资源就可以通过MessageSource提供的方法
 *      实现key与value的设置与返回
 *      1.判断在BeanFactory中是否存在一个MessageSource对象，如果存在会根据判断来决定是否要进行父资源的结构配置（层次的资源管理）
 *      2.如果不存在MessageSource对象实例，那么则新建一个MessageSource对象实例，并且保存在BeanFactory对象中（单例保存）
 *
 *
 *
 *  initApplicationEventMulticaster()
 *      初始化事件广播
 *      与MessageSource初始化流程非常相似，通过该方法，Spring内部就有了一个
 *      事件广播处理器，从而实现所有事件的发布处理
 *
 *
 *  registerListeners()
 *      在该方法中，首先获取所有的监听器，然后将监听器注册到ApplicationEventMulticaster中
 *      然后在获取所有的早期事件，将其进行事件广播
 *
 *  finishBeanFactoryInitialization(beanFactory)
 *      完成BeanFactory的处理方法
 *      注册所有的非延迟操作的Bean对象
 *      即除了一些系统内置的Bean对象和BeanFactoryPostProcessor以外的
 *      非懒加载的Bean对象都是在这个方法中完成实例化的。同时BeanPostProcessor的
 *      方法也是在此方法中调用的
 *
 *
 *   finishRefresh()
 *      发布响应事件
 *
 *
 *  AnnotationConfigApplicationContext
 *      注解方式启动，观察其构造方法：
 *      public AnnotationConfigApplicationContext()
 *      {
 *          StartupStep createAnnotatedBeanDefReader = this.getApplicationStartup()
 *           .start("spring.context.annotated-bean-reader.create");
 *          this.reader = new AnnotatedBeanDefinitionReader(this); // 注解配置类的读取
 *          createAnnotatedBeanDefReader.end();
 *          this.scanner = new ClassPathBeanDefinitionScanner(this); // ClassPath中Bean定义扫描处理
 * }
 *      public AnnotationConfigApplicationContext(Class<?>... componentClasses)
 *      { // 组件类
 *          this(); // 无参构造
 *          register(componentClasses);
 *          refresh();
 *      }
 *      public AnnotationConfigApplicationContext(String... basePackages) { // 扫描包
 *          this(); // 无参构造
 *          scan(basePackages);
 *          refresh();
 *      }
 *
 *
 *  由以上可知，它的构造方法核心为无参构造，无参构造中存在两个类
 *  this.reader = new AnnotatedBeanDefinitionReader(this); // 注解配置类的读取
 *  this.scanner = new ClassPathBeanDefinitionScanner(this); // ClassPath中Bean定义扫描处理
 *  有参构造中存在有以下方法
 *  register(componentClasses)
 *  scan(basePackages)
 *
 * */

public class StartSpringApplication  {
    private static final Logger LOGGER= LoggerFactory.getLogger(StartSpringApplication.class);
    public static void main(String[] args)throws Exception {
        //firstDemo();
        //i18nTest();
        //initDestroyTest();
        //initDestroyTest2();
        //initDestroyTest3();
        //initDestroyTest4();
        //xmlBeanDefinitionReaderTest();
        //entityResolverTest();
        //beanDefinitionTest();
        //beanDefinitionParserDelegateTest();
        //listBeanFactoryTest();
        //configurableBeanFactoryTest();
        //objectProviderTest();
        //factoryBeanTest();
        //beanFactoryPostProcessorTest();
        //BeanLifeCycleTest();
        //AwareTest();
        test();










    }

    private static void firstDemo(){
        ApplicationContext context= new ClassPathXmlApplicationContext("spring/application.xml");
        IMessageService service=context.getBean("ImessageService",IMessageService.class);
        System.out.println("响应结果："+service.echo("jayjxia"));

    }

    /**
     * 通过MessageSource获取国际化文件中的属性
     * */
    private static void i18nTest(){
        ResourceBundleMessageSource messageSource=new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n.Message");
        LOGGER.info("【默认的Message】{}",messageSource.getMessage("name",null,null));//由于是在中文的操作系统下，所以获取到的结果和中文的Message是相同的
        LOGGER.info("【中文的Message】{}",messageSource.getMessage("name",null,new Locale("zh","CN")));
        LOGGER.info("【英文的Message】{}",messageSource.getMessage("name",null,new Locale("en","US")));


    }
    /**
     * 测试使用xml方式进行Bean对象初始化和销毁方法的指定
     * */
    private static void initDestroyTest(){
        ClassPathXmlApplicationContext context=new ClassPathXmlApplicationContext("spring/application.xml");
        MyBean bean=(MyBean) context.getBean("MyBean1");
        bean.onRecive("jayjxia");
        context.registerShutdownHook();//关闭注册
        //context.close();//销毁Bean对象，和registerShutdownHook实现的功能相同，里面都是调用了doClose()方法

    }
    private static void initDestroyTest2(){
       AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext();
       context.scan("com.maker.vo");

       context.refresh();
        MyBean2 bean=(MyBean2) context.getBean("MyBean2");
        bean.onRecive("jayjxia");
        context.registerShutdownHook();//关闭注册
        //context.close();//销毁Bean对象，和registerShutdownHook实现的功能相同，里面都是调用了doClose()方法

    }

    private static void initDestroyTest3(){
        AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext();
        context.scan("com.maker.vo");

        context.refresh();
        MyBean3 bean=(MyBean3) context.getBean("MyBean3");
        bean.onRecive("jayjxia");
        context.registerShutdownHook();//关闭注册
        //context.close();//销毁Bean对象，和registerShutdownHook实现的功能相同，里面都是调用了doClose()方法

    }
    private static void initDestroyTest4(){
        AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext();
        context.scan("com.maker.vo");

        context.refresh();
        context.start();//如果不调用此方法，无法调用LifeCycle中的start方法
        MyBean4 bean=(MyBean4) context.getBean("MyBean4");
        bean.onReceive("jayjxia");
        context.registerShutdownHook();//关闭注册
        //context.close();//销毁Bean对象，和registerShutdownHook实现的功能相同，里面都是调用了doClose()方法

    }


    private static void xmlBeanDefinitionReaderTest(){
        BeanDefinitionRegistry registry=new AnnotationConfigApplicationContext();
        XmlBeanDefinitionReader reader=new XmlBeanDefinitionReader(registry);
        int count=reader.loadBeanDefinitions("classpath:spring/application.xml");
        //可以利用该类，获取到Spring中所注册的所有Bean的数量
        LOGGER.info("定义的Bean数量(包含包扫描注解的Bean对象)={}",count);
    }

    private static void entityResolverTest() throws Exception{
        BeanDefinitionRegistry registry=new AnnotationConfigApplicationContext();
        XmlBeanDefinitionReader reader=new XmlBeanDefinitionReader(registry);
        ResourceEntityResolver resolver=new ResourceEntityResolver(reader.getResourceLoader());
        String PublicId="http://www.springframework.org/schema/context";
        String SystemId="https://www.springframework.org/schema/context/spring-context-4.3.xsd";
        InputSource input=resolver.resolveEntity(PublicId,SystemId);
        Scanner scanner=new Scanner(input.getByteStream());
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        while(scanner.hasNext()){
            String line=scanner.next()+"\n";
            bos.write(line.getBytes());
        }
        LOGGER.info("【实体解析内容】{}",bos);

    }
    /**
     * 通过BeanDefinition可以获取到Bean对象被解析后的所有信息
     *
     * */
    private static void beanDefinitionTest(){
        AnnotationConfigApplicationContext application=new AnnotationConfigApplicationContext();
        application.scan("com.maker.vo");
        application.refresh();
        //通过Bean名称获取到该Bean所有解析的内容
        BeanDefinition definition=application.getBeanDefinition("MyBean3");
        LOGGER.info("Bean的类名称={}",definition.getBeanClassName());
        LOGGER.info("scope={}",definition.getScope());
        LOGGER.info("初始化方法={},销毁方法={}",definition.getInitMethodName(),definition.getDestroyMethodName());
    }
    /**
     * 解析Spring配置项
     *  1.XmlBeanDefinitionReader首先可以进行判断XML配置文件中是否
     *  含有Bean元素，有Bean元素才进行解析
     *  2.通过DocumentLoader获取XML配置文件的Document，通过Document遍历bean元素
     *  3.利用BeanDefinitionParserDelegate对每个bean元素进行解析，并将信息存储到BeanDefinitio中
     *
     * */
    private static void beanDefinitionParserDelegateTest()throws Exception{
        BeanDefinitionRegistry registry=new AnnotationConfigApplicationContext();
        XmlBeanDefinitionReader reader=new XmlBeanDefinitionReader(registry);
        int count=reader.loadBeanDefinitions("classpath:spring/application.xml");
        if(count>0){//存在有Bean才继续进行解析
            Document doc=getDoc(reader);//获取XML Document对象
            NodeList beanlist=doc.getElementsByTagName("bean");
            //对Bean元素进行解析，此类初始化需要有一个XmlReaderContext对象
            BeanDefinitionParserDelegate parserDelegate=new BeanDefinitionParserDelegate(getReaderContext(reader));
            for(int i=0;i<beanlist.getLength();i++){
                Element bean=(Element) beanlist.item(i);
                //解析Bean元素信息
                BeanDefinitionHolder holder=parserDelegate.parseBeanDefinitionElement(bean);
                //将解析到的信息存放在BeanDefinition中
                BeanDefinition definition=holder.getBeanDefinition();
                LOGGER.info("Bean id={}、class={}",bean.getAttribute("id"),definition.getBeanClassName());

            }
        }
    }
    /**
     * 获取XML配置文件Document对象
     *
     * */
    private static Document getDoc(XmlBeanDefinitionReader reader) throws Exception{
        ResourceEntityResolver resolver=new ResourceEntityResolver(reader.getResourceLoader());
        reader.setEntityResolver(resolver);
        //通过该接口实例，返回Document文档对象
        DocumentLoader loader=new DefaultDocumentLoader();//文档加载器
        InputSource inputSource=new InputSource(reader.getResourceLoader().getResource("classpath:spring/application.xml").getInputStream());
        Document doc=loader.loadDocument(inputSource,resolver,new DefaultHandler(), XmlValidationModeDetector.VALIDATION_XSD,true);
        return doc;
    }
    /**
     * 获取XmlReaderContext对象
     * */
    private static XmlReaderContext getReaderContext(XmlBeanDefinitionReader reader){
        XmlReaderContext context=reader.createReaderContext(reader.getResourceLoader().getResource("classpath:spring/application.xml"));
        return context;
    }

    private static void listBeanFactoryTest(){//ApplicationContext是ListableBeanFactory的子接口
       // ListableBeanFactory beanFactory=new AnnotationConfigApplicationContext();
        AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext();
        context.scan("com.maker.vo");
        context.refresh();
        LOGGER.info("Spring容器中Bean的数量:{}",context.getBeanDefinitionCount());
        LOGGER.info("Spring容器中存在的Bean名称：{}", Arrays.toString(context.getBeanDefinitionNames()));
        LOGGER.info("Spring容器中拥有Component注解的Bean：{}",Arrays.toString(context.getBeanNamesForAnnotation(Component.class)));

    }

    private static void configurableBeanFactoryTest(){
        AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext();
        context.scan("com.maker.vo");
        context.refresh();
        ConfigurableBeanFactory beanFactory=context.getBeanFactory();
        LOGGER.info("获取单例对象{}",beanFactory.getSingleton("memberSingleton"));
        LOGGER.info("获取单例对象{}",beanFactory.getSingleton("memberSingleton"));
        LOGGER.info("获取单例对象{}",beanFactory.getSingleton("memberSingleton"));
        LOGGER.info("获取原型对象{}",beanFactory.getSingleton("memberPrototype"));
        LOGGER.info("获取原型对象{}",beanFactory.getBean("memberPrototype"));
        LOGGER.info("单例Bean个数{}",beanFactory.getSingletonCount());




    }
    /**
     * ObjectProvider操作
     *  可以结合函数式编程，在多个同类型Bean中选择一个Bean进行注入
     * */
    private static void objectProviderTest(){
        AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext();
        context.register(DeptConfig.class);
        context.refresh();
        ObjectProvider<DeptDao> provider=context.getBeanProvider(DeptDao.class);//获取到了此类型的Bean
        LOGGER.info("【Provider实现类】{}",provider.getClass());//DefaultListableBeanFactory
        for(DeptDao deptDao : provider){
            LOGGER.info("【DeptDao对象】{}",deptDao);
        }
        //DeptDao的实现类，定义了两个Bean，如果直接进行自动注入，肯定是无法成功的
        //可以通过ObjectProvider选择多个Bean中的一个，进行注入
        DeptDao deptDao=provider.stream().findFirst().orElse(null);//找到第一个Bean对象，如果为null则设置为null
        LOGGER.info("获取到Bean对象：{}",deptDao);
    }

    /**
     * FactoryBean操作
     *  Spring中，考虑到节约JVM的空间，同时考虑到GC回收变量的效率问题，
     *  大多数Bean都是以单例的形式存在的，而某些场景如：有一个Dept类，Spring中
     *  需要保存有不同Dept对象，这个时候就可以使用到FactoryBean来进行实现了
     * */
    public static void factoryBeanTest() throws Exception{
        AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext();
        context.scan("com.maker.factory");
        context.refresh();
        //通过BeanFactory获取到FactoryBean
        FactoryBean<Dept> factory=context.getBean("&deptFactoryBean",FactoryBean.class);
        LOGGER.info("获取Dept对象：{}",factory.getObject());
        LOGGER.info("获取Dept对象：{}",factory.getObject());

    }
    /**
     * 在config包下创建一个MessageChannel类，创建一个MessageChannelConfig类
     * 创建一个processor.MessageChannelPostProcessor类，此类为BeanFactoryPostProcessor的实现类
     *
     * */
    private static void beanFactoryPostProcessorTest(){
        AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext();
        //扫描到相应的Processor实现类后，Spring在初始化完成后会自动调用该类职工的方法
        context.scan("com.maker.processsor");
        context.register(MessageChannelConfig.class);
        context.refresh();
        MessageChannel channel=context.getBean(MessageChannel.class);
        LOGGER.info("获取到的MessageChannel对象为：{}",channel);


    }
    /**
     * 观察一个Bean实例初始化会经历的过程
     *  由Spring容器启动能看出Bean对象初始化流程大致如下：
     *      BeanFactoryPostProcessor-postProcessBeanFactory()最先执行，它通过BeanFactory中的BeanDefinition获取到所有Bean类的信息，但还未进行实例化
     *      构造方法被调用，进行具体每个Bean对象的实例化
     *      BeanPostProcessor-postProcessBeforeInitialization()被调用，Bean对象初始化签调用
     *      InitializingBean-afterPropertiesSet()被调用
     *      BeanPostProcessor-postProcessAfterInitialization()被调用，初始化之后调用
     *      SmartInitializingSingleton-afterSingletonsInstantiated()被调用，最后调用，初始化完成
     *
     *
     *
     * */
    private static void BeanLifeCycleTest(){
        AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext();
        context.scan("com.maker.lifecycle");
        context.refresh();

    }

        private static void AwareTest(){
        AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext();
        context.scan("com.maker.aware");
        context.refresh();
            GetDatabaseConfig databaseConfig=context.getBean(GetDatabaseConfig.class);
            LOGGER.info("【DatabaseConfig】{}",databaseConfig.getConfig());
    }

    private static void test(){
        AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext();
        context.scan("com.maker.vo");
        context.refresh();
        Book_SpEL book=context.getBean(Book_SpEL.class);
        LOGGER.info("【对象信息】{}",book);
    }
}
