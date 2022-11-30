package com.maker.config;


//import jakarta.persistence.spi.PersistenceProvider;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.SharedCacheMode;
import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:config/jpa.properties")
public class SpringDataJPAConfig {
    @Value("${hibernate.hbm2ddl.auto}")
    private String hbm2ddlAuto;
    @Value("${hibernate.show_sql}")
    private boolean showSql;
    @Value("${hibernate.format_sql}")
    private String formatSql;
    //@Value("${hibernate.cache.use_second_level_cache}")
    //private boolean secondLevelCache;
    //@Value("${hibernate.cache.region.factory_class}")
    //private String factoryClass;
   // @Value("${hibernate.javax.cache.provider}")
    //private String cacheProvider;
    @Bean("entityManagerFactory") //Bean Id必须为这个
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(
            DataSource dataSource,//之前配置的HikariCP
            HibernatePersistenceProvider provider,
            HibernateJpaVendorAdapter adapter,
            HibernateJpaDialect dialect
    ){  //工厂配置
        LocalContainerEntityManagerFactoryBean factoryBean=new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);

        factoryBean.setPersistenceProvider(provider);
        factoryBean.setJpaVendorAdapter(adapter);
        factoryBean.setJpaDialect(dialect);
        factoryBean.setSharedCacheMode(SharedCacheMode.ENABLE_SELECTIVE);//启用二级缓存
        factoryBean.setPackagesToScan("com.maker.po");
        factoryBean.setPersistenceUnitName("jayjxia");//持久化单位配置
        factoryBean.getJpaPropertyMap().put("hibernate.hbm2ddl.auto",hbm2ddlAuto);

        factoryBean.getJpaPropertyMap().put("hibernate.format_sq",formatSql);
//        factoryBean.getJpaPropertyMap().put("hibernate.cache.use_second_level_cache",secondLevelCache);
//        factoryBean.getJpaPropertyMap().put("hibernate.cache.region.factory_class",factoryClass);
//        factoryBean.getJpaPropertyMap().put("hibernate.javax.cache.provider",cacheProvider);

        return factoryBean;
    }
    @Bean
    public HibernatePersistenceProvider provider(){
        return new HibernatePersistenceProvider();
    }
    @Bean
    public HibernateJpaDialect dialect(){
        return new HibernateJpaDialect();
    }
    @Bean
    public HibernateJpaVendorAdapter adapter(){
        HibernateJpaVendorAdapter adapter=new HibernateJpaVendorAdapter();
        adapter.setShowSql(showSql);
        return adapter;
    }


}
