package com.maker.config;

import com.maker.dao.impl.DeptDaoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeptConfig {
    @Bean
    public DeptDaoImpl getDeptDao1(){
        return new DeptDaoImpl();
    }
    @Bean
    public DeptDaoImpl getDeptDao2(){
        return new DeptDaoImpl();
    }


}
