package com.maker.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.*;
import org.springframework.util.ClassUtils;

import java.io.*;
import java.net.URL;
import java.util.Scanner;

/**
 * Resource接口的使用
 *
 *  Spring注入Resource接口实例
 *      1.创建resource.MessageResource类
 *      2.在application.xml中创建MessageResource Bean对象，并且注入Resource属性
 *      3.在测试类中进行测试
 *
 *
 *  路径通配符
 *      所谓的通配符就是指的一次可以加载多种资源，之前使用Resource接口处理的时候，都是通过
 *      字符串的形式进行定义的，但是之前的定义只能够加载一种资源，现在如果要进行一些资源的匹配处理
 *      那么就通过资源通配符来完成
 *          ?:匹配任意的零位或一位字符
 *          *:匹配任意的零位或多位字符
 *          **:匹配任意的目录
 *
 *       创建一个MessageResource2，那进行多个资源的注入
 *
 *       通过classpath*加上通配符的操作，还可以读取jar包中的资源文件
 *
 * */
public class ResourceTest {
    private static final Logger LOGGER=  LoggerFactory.getLogger(ResourceTest.class);
    public static void main(String[] args) {
        try{
//            ByteArrayResourceTest();
//            FileResourceTest();
//            URLResourceTest();
            //getClassPath();
            //ClassPathResourceTest();
            //writeableResourceTest();
            resourceLoader();
        }catch(Exception e){
            LOGGER.error("出现异常：{}",e);
        }
    }

    /**
     * 读取内存资源
     * */
    private static void ByteArrayResourceTest()throws Exception{
        byte[] data="jayjxia".getBytes();
        Resource resource=new ByteArrayResource(data);
        InputStream input=resource.getInputStream();
       // byte[] result=new byte[128];
        OutputStream out=new ByteArrayOutputStream();
        int temp=0;
        while((temp=input.read())!=-1){
            out.write(Character.toUpperCase(temp));

        }
        input.close();
        System.out.println(out);
        out.close();

    }
    /**
     * 读取文件资源
     * */
    private static void FileResourceTest()throws Exception{
        String filePath="D:"+ File.separator+"abc.txt";
        Resource resource= new FileSystemResource(filePath);
        File target=resource.getFile();
        FileInputStream inputStream=new FileInputStream(target);
        byte[] data=new byte[128];
        int length=0;
        while((length=inputStream.read(data,0,data.length))!=-1){
            System.out.println(target.getName()+"文件内容："+new String(data,0,length));
        }
    }
    /**
     * 读取URL资源
     * */
    private static void URLResourceTest() throws Exception{
        String URL="https://www.baidu.com";
        Resource resource=new UrlResource(URL);
        InputStream inputStream=resource.getInputStream();
        byte[] data=new byte[128];
        int temp=0;
        while((temp=inputStream.read(data,0,data.length))!=-1){
            System.out.println(new String(data,0,temp));
        }

    }

    /**
     * 通过Resource读取ClassPath下资源文件
     *  调用 ClassPathResource()的单参构造，源码会调用：
     *      this(path, (ClassLoader) null);的构造
     *      如果ClassLoader为null，那么多参构造会进行如下操作：
     *      this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
     *      对比可知ResourceTest.class.getClassLoader()和ClassUtils.getDefaultClassLoader()所得到的ClassLoader是相同的对象
     *      本质上来说，读取资源文件利用的是ClassLoader，若传入了具体的Class对象，那么可以根据this.clazz.getResourceAsStream(this.path)
     *      来进行资源的读取
     *
     *
     * */
    private static void ClassPathResourceTest() throws Exception{
        System.err.println("ClassLoader1："+ResourceTest.class.getClassLoader());
        System.err.println("ClassLoader2："+ ClassUtils.getDefaultClassLoader());
        Resource resource=new ClassPathResource("abc.txt");
        System.out.println("文件路径："+resource.getFile());
        InputStream input=resource.getInputStream();
        byte[] data=new byte[128];
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        int temp=0;
        while((temp=input.read(data,0,data.length))!=-1){
            out.write(data,0,temp);
            System.out.println(out);
        }
    }
    /**
     * 获取项目路径的传统方法
     *  1.user.dir
     *  2.class.getClassLoader().getResource("")
     * */
    private static void getClassPath(){
        System.out.println("1、项目路径："+System.getProperty("user.dir"));
        System.out.println("2、项目路径："+ResourceTest.class.getClassLoader().getResource(""));

    }
    /**
     * 可写入资源
     *  在Spring 3中开始提供WritableResource接口
     *  进行资源写入的时候可以实现传统IO和NIO的资源写入处理
     *      1.利用WritableResource写入数据到文件中
     *
     * */
    private static void writeableResourceTest() throws Exception {
        Resource resource=new ClassPathResource("abc.txt");
        File file=resource.getFile();
        System.out.println(file);
        WritableResource writableResource=new FileSystemResource(file);
        OutputStream out=writableResource.getOutputStream();
        for (int i=0;i<5;i++){
            out.write("jayjxia\r\n".getBytes());
        }
        out.close();
    }
    /**
     * ResourceLoader
     *  Spring 的核心思想在于所有对象的实例化，都不要直接使用关键字new完成，
     *  都应该通过相应的工厂完成
     *  那么对于Resource的实例化，Spring中提供了一个ResourceLoader
     *  ResourceLoader接口部分源码:
     *      String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;
     *      Resource getResource(String location);
     *      ClassLoader getClassLoader();
     *  ResourceUtils抽象类部分源码：
     *      public static final String CLASSPATH_URL_PREFIX = "classpath:";
     *
     *  ResourceLoader的实现子类中，包含有一个ClassPathXmlApplicationContext实现类
     *  也可以使用DefaultResourceLoader来实例化接口
     *
     *  由以下的实例可知，在获取Resource子类的时候，根据Location的前缀字符串就可以自动的
     *  获取到不同资源类型的Resource实例，这个本质上就是一种工厂设计模式的思想。
     *
     *
     *
     * */
    private static void resourceLoader() throws Exception{

        ResourceLoader resourceLoader=new DefaultResourceLoader();
        //实现文件数据的加载
        //Resource resource=resourceLoader.getResource("file:D:\\abc.txt");
        //实现ClassPath资源的读取
        //Resource resource=resourceLoader.getResource("classpath:abc.txt");
        //时间URL资源的读取
        Resource resource=resourceLoader.getResource("https://www.baidu.com");
        Scanner scanner=new Scanner(resource.getInputStream());
        scanner.useDelimiter("\n");
        while(scanner.hasNext()){
            String result=scanner.next();
            System.out.println(result);

        }
        scanner.close();
    }
}
