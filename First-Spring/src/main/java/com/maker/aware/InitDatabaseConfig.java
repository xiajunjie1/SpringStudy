package com.maker.aware;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitDatabaseConfig {
    @Bean
    public DatabaseConfig databaseConfig(){
        DatabaseConfig config=new DatabaseConfig();
        config.setName("jayjxia");
        config.setUrl("localhost:3306/jayjxia");
        return config;
    }
   @Bean
    public GetDatabaseConfig getDatabaseConfig(){
        return new GetDatabaseConfig();
    }
}
