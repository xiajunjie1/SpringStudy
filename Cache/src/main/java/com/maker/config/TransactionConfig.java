package com.maker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class TransactionConfig {
    /**
     * 此处要使用SpringDataJPA就需要用JpaTransactionManager代替DataSourceTransactionManager子类
     * 否则无法执行更新操作
     * */
    @Bean
    public PlatformTransactionManager transactionManager(@Autowired DataSource dataSource){
        JpaTransactionManager transactionManager=new JpaTransactionManager();//PlatformTransactionManager接口的实现子类
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }
}
