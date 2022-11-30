package com.maker.main;

import com.maker.aop.config.AopConfig;
import com.maker.aop.service.IMessageService;
import com.maker.aop.service.impl.MessageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Aop编程（面向切面编程）
 *  Aop基于动态代理的设计模式（Java动态代理+Cglib）
 *  关注点：主业务代码，即需要在哪个方法，引入动态代理
 *  横切关注点：具体的代理增加的辅助功能
 *  织入：即将代理的方法和主业务方法
 *
 * Aop通知类型：
 *  1.前置通知
 *      关注点前执行的横切关注点
 *  2.后置通知
 *      后置返回通知：核心方法返回之前执行
 *      后置异常通知：核心处理方法抛出异常执行
 *      后置最终通知：不管是否出现问题都执行该通知
 *  3.环绕通知
 *      核心处理方法之前和之后都执行该通知
 *
 *
 *
 * AspectJ表达式定义
 *  这种表达式一般都要写在专属的AOP配置类之中
 *  Spring支持的AspectJ切入点标识符如下：
 *      execution:定义通知的切入点
 *      this:用于匹配当前AOP代理对象类型的执行方法
 *      target:用于匹配当前目标对象类型的执行方法
 *      args:用于匹配当前执行的方法传入的参数为指定类型的执行方法
 *
 *  一般开发来讲，如果想匹配所有的业务方法（业务类一般而言都会放在service包下），一般可以按如下的方式定义
 *      1.匹配包名：com.maker..service：代表最后一个包是service，可能是com.maker.serivce也可能是com.maker.oa.service等等，只要满足这种形式的包都会被匹配到
 *      2.匹配方法名：com.maker..service..*.*(..)：可以匹配到service（含其子包下）所有类的所有方法（*.*(..)）
 *      3.每一个方法都要访问权限，也都有返回值，继续匹配：public * com.maker..service..*.*(..),一般可以调用的方法都是public修饰，这里匹配任意返回值类型所以用*来指代
 *      4.表达式需要通过execution包装执行：execution(public * com.maker..service..*.*(..))
 *
 *
 *  AOP的基础实现
 *      通过XML文件的方式编写AOP
 *      1.修改SpringDemo的build.gradle文件，进行依赖的导入，需要导入Spring-AOP和Spring-Aspect
 *      2.创建IMessageService接口及其实现子类
 *      3.创建advice子包，在当中创建AOP处理类ServiceAdvice
 *      4.在spring/beans.xml中追击AOP的支持
 *      5.编写测试方法进行AOP测试
 *
 *
 *  AOP配置深入
 *      AOP的实现中，有两种代理模式：JDK动态代理（基于接口的代理），Cglib动态代理（基于类的代理）
 *      对于常规的业务层的类开发，采用JDK的动态设计模式很方便，但是如果要是对于一些控制层的开发进行代理设计，
 *      那么就要用到Cglib了。
 *      那么如何配置当前项目中的代理类型呢？还是所有的代理类型都是由Spring自动进行分辨呢
 *      这就需要观察aop的配置文件
 *      <aop:config >标签中，存在一个属性：proxy-target-class=""，如果将它配置为true，那么就是采用的Cglib的代理
 *      默认此属性为false
 *      在springMVC中有一种全局异常处理的形式，此处就要使用类代理
 *
 *  通知参数接收
 *      对应IMessageService当中的echo来讲，是提供了一个参数的
 *      但是在进行前置通知以及后置通知的时候，我们会发现我们有能够进行参数的接收处理
 *      那么这样的代理是没有太大意义的存在的，如果想要实现参数的接收，需要修改代理类中通知的处理方法
 *          1.在代理类中，为处理方法追加对应的参数
 *          2.修改配置的切面表达式，在切面表达式中进行参数的定义
 *
 *      后置通知：
 *          <aop:after />：最终通知
 *          <aop:after-throwing />：后置异常通知，它里面会比最终通知多出throwing属性和args-names属性，用来接收异常信息，并给通知方法的参数列表，进行接收
 *          <aop:after-returning />：后置返回通知，它里面会比最终通知多出returning属性和arg-names属性，用来接收返回信息，并给通知方法的参数列表，进行接收
 *
 *      环绕通知：
 *          在使用环绕通知的时候，需要注意一个核心程序类ProceedingJoinPoint类，这个类可以获取到
 *          调用的全部信息，例如：在方法调用的时候，所传递的参数内容都可以通过此类获取
 *          环绕通知在结构上更加接近于传统的代理模式，使用起来更为简单
 *
 *
 *  基于Annotation完成AOP的配置
 *      AOP核心类：org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator
 *      1.通过XML启用：在beans.xml中添加<aop:aspectj-autoproxy />
 *      2.通过注解启用：使用注解@EnableAspectJAutoProxy进行AOP注解的启用
 *          在config包下定义AopConfig类，在该配置类上使用上述注解
 *      在advice包中编写ServiceAdvice类
 *
 *
 *  @EnableAspectJAutoProxy注解
 *      proxyTargetClass:指定代理的类型
 *      exposeProxy：默认为false，表示不对外暴露代理类的实例
 *          在进行代理调用的时候，对外的显示实际上都是某些接口生成的代理类实例
 *          但是要想对外进行代理类对象的暴露，就需要对当前的属性进行配置
 *          它的意思就是是否需要将AOP代理的对象直接提供给用户，如果选择true，那么
 *          用户就可以直接通过AOPContext获取到代理的对象实例
 *          
 * */

public class AopSpringApp {
    private static final Logger LOGGER= LoggerFactory.getLogger(AopSpringApp.class);

    public static void main(String[] args){
       // firstAop();
        annotationAop();
    }

    private static void firstAop(){
        ApplicationContext context= new ClassPathXmlApplicationContext("classpath:spring/beans.xml");
        IMessageService messageService=  context.getBean(IMessageService.class);
        messageService.echo("AOP 测试");

    }

    private static void annotationAop(){
        AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext();
        context.register(AopConfig.class);
        context.refresh();
        IMessageService messageService=  context.getBean(IMessageService.class);
        messageService.echo("AOP 测试");

    }
}
