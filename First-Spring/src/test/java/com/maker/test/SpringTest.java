package com.maker.test;

import com.maker.di.collections.AllConfigs;
import com.maker.di.config.MessageConfig;
import com.maker.di.config.MessageConfig_2;
import com.maker.di.config.MessageConfig_copy;
import com.maker.di.config.MessageProperties;
import com.maker.resource.MessageResource;
import com.maker.resource.MessageResource2;
import com.maker.service.IDeptService;
import com.maker.service.IMessageService;
import com.maker.service.MessageService;
import com.maker.vo.Book_SpEL;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

//加载配置文件，后期也可能是配置类
@ContextConfiguration(locations = {"classpath:spring/application.xml"})
@ExtendWith(SpringExtension.class) //表示此时使用外部的测试工具即Junit5
public class SpringTest {
    @Autowired
    private IMessageService service;
    @Autowired
    @Qualifier("MessageService")
    private MessageService service2;

    @Autowired
    private MessageConfig_copy config_copy;
    @Autowired
    @Qualifier("allconfigs")
    private AllConfigs configs;
    @Autowired
    @Qualifier("messageProperties")
    private MessageProperties messageProperties;

    private static final Logger logger= LoggerFactory.getLogger(SpringTest.class);//获取日志实例
    @Autowired
    private IDeptService deptService;
    //@Autowired
    //private MessageConfig_2 config2; //报错，因为在BeanConfig配置类当中注册了两个同类型的Bean对象
    @Autowired
    private MessageConfig_2 messageConfigMain;//正常注入，因为根据变量名字和Bean名称进行了自动匹配
    @Autowired
    @Qualifier("messageConfigBak")
    private MessageConfig_2 config2;//正常注入
    @Autowired
    private MessageResource messageResource;
    @Autowired
    private MessageResource2 messageResource2;

    @Test
    public void firstTest(){
        logger.debug("Debug响应：{}",service.echo("jayjxia"));
        logger.info("Info响应：{}",service.echo("jayjxia"));
        logger.error("error响应：{}",service.echo("jayjxia"));

    }

    @Test
    public void testSend(){
        service2.send("Spring DI 测试");
    }
    @Test
    public void testConstructorDi(){
        logger.info("生成的对象为：{}",config_copy);
    }
    @Test
    public void testCollectionsDi(){
        List<MessageConfig> messageConfigList=configs.getMconfigs();
        logger.info("【List】集合结果：{}、实现类类型：{}",messageConfigList,messageConfigList.getClass());
        logger.info("【Set】集合结果：{}、实现类类型：{}",configs.getMsets(),configs.getMsets().getClass());
        logger.info("【Map】集合结果：{}、实现类类型：{}",configs.getMaps(),configs.getMaps().getClass());
        logger.info("【properties】集合结果：{}",messageProperties.getAttribute());
    }
    @Test
    public void testAnnotation(){
        logger.info("部门信息：{}",deptService.getDept(10L));
    }
    @Test
    public void testAnnotationDi(){
        logger.info("对象信息：{}",messageConfigMain);
        logger.info("对象信息：{}",config2);
    }

    @Test
    public void testResource() throws Exception{
        logger.info("Resource实现类型：{}",messageResource.getResource().getClass());
        Resource resource= messageResource.getResource();
        Scanner scanner=new Scanner(resource.getInputStream());
        scanner.useDelimiter("\n");
        StringBuffer buffer=new StringBuffer();
        while(scanner.hasNext()){
            buffer.append(scanner.next());
        }
        logger.info("读取结果{}",buffer);
        scanner.close();

    }
    @Test
    public void testResource2() throws Exception{
        //同时读取多个资源
        Resource[] resources=this.messageResource2.getResources();
        Resource[] resources2=this.messageResource2.getResources2();
        //System.err.println("【集合结果】"+ Arrays.toString(resources));
        for(Resource res : resources){
            logger.info("资源文件名称：{}",res.getFile());
        }

        for(Resource res : resources2){
            logger.info("Jar文件名称：{}",res);
        }
    }
    @Test
    public void testSpEL( @Autowired Book_SpEL book_SpEL){
        logger.info("对象信息为：{}",book_SpEL);
    }
}
