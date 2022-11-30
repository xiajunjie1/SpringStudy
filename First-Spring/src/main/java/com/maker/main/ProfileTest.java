package com.maker.main;

import com.maker.di.config.MessageConfig_Profile;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
/**
 * 此环境的切换可以通过gradle，修改build.gradle中的
 * def env = System.getProperty("env") ?: 'dev'，可以将'dev'换成其他的环境
 * */
public class ProfileTest {
    public static void main(String[] args) {
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext("spring/application.xml");
        MessageConfig_Profile profile=applicationContext.getBean(MessageConfig_Profile.class);
        System.out.println("当前对象为："+profile);
        System.out.println("当前环境为："+ Arrays.toString(applicationContext.getEnvironment().getActiveProfiles()));
    }
}
