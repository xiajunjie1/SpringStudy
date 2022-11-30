package com.maker;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ComponentScan("com.maker")
@EnableTransactionManagement//开启事务支持（为了使用@Transactional注解）
@EnableJpaRepositories("com.maker.dao")//数据层代码简化的关键
public class SpringDataJPAStart {
}
