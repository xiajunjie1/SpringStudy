package com.maker.jdbc.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:config/database.properties")
public class HikariCPDataSourceConfig {
    @Value("${mydb.database.driverClassName}")
    private String driverClassName;
    @Value("${mydb.database.jdbcUrl}")
    private String jdbcUrl;
    @Value("${mydb.database.username}")
    private String username;
    @Value("${mydb.database.password}")
    private String password;
    @Value("${mydb.database.connectionTimeOut}")
    private long connectionTimeOut;
    @Value("${mydb.database.readOnly}")
    private boolean readOnly;
    @Value("${mydb.database.pool.idleTimeOut}")
    private long idleTimeOut;
    @Value("${mydb.database.pool.maxLifeTime}")
    private long maxLifeTime;
    @Value("${mydb.database.pool.maximumPoolSize}")
    private int maximumPoolSize;
    @Value("${mydb.database.pool.minimumIdle}")
    private int minimumIdle;
    /**
     * 初始化HikariCP数据库连接池，并且将其注册到Spring当中
     *
     * */
    @Bean
    public DataSource hikariCpDataSource(){
        HikariDataSource dataSource=new HikariDataSource();
        dataSource.setDriverClassName(this.driverClassName);
        dataSource.setJdbcUrl(this.jdbcUrl);
        dataSource.setUsername(this.username);
        dataSource.setPassword(this.password);
        dataSource.setConnectionTimeout(this.connectionTimeOut);
        dataSource.setReadOnly(this.readOnly);
        dataSource.setMaximumPoolSize(this.maximumPoolSize);
        dataSource.setMaxLifetime(this.maxLifeTime);
        dataSource.setMinimumIdle(this.minimumIdle);
        dataSource.setIdleTimeout(this.idleTimeOut);
        return dataSource;
    }
    @Bean
    public JdbcTemplate jdbcTemplate(@Autowired DataSource dataSource){
        JdbcTemplate template=new JdbcTemplate();
        template.setDataSource(dataSource);
        return template;
    }
    @Bean
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource){
        DataSourceTransactionManager transactionManager=new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }
}
