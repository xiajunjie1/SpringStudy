package com.maker.config;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * 事务增强的配置（配置事务拦截器和事务织入）
 * */
@Configuration
@Aspect
public class TransactionAdviceConfig {
    @Bean
    public TransactionInterceptor transactionInterceptor(@Autowired TransactionManager transactionManager){
        //事务属性的配置，只读属性的配置
        RuleBasedTransactionAttribute readAttribute=new RuleBasedTransactionAttribute();
        RuleBasedTransactionAttribute requiredAttribute=new RuleBasedTransactionAttribute();
        readAttribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);//传播机制,非事务运行
        readAttribute.setReadOnly(true);
        requiredAttribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        Map<String, TransactionAttribute> mapAttribute=new HashMap<>();
        //保存方法名称映射
        mapAttribute.put("add*",requiredAttribute);
        mapAttribute.put("edit*",requiredAttribute);
        mapAttribute.put("remove*",requiredAttribute);
        mapAttribute.put("get*",readAttribute);
        mapAttribute.put("find*",readAttribute);
        mapAttribute.put("save*",requiredAttribute);
        //创建名称匹配事务
        NameMatchTransactionAttributeSource source=new NameMatchTransactionAttributeSource();
        source.setNameMap(mapAttribute);
        TransactionInterceptor interceptor=new TransactionInterceptor();
        interceptor.setTransactionManager(transactionManager);
        interceptor.setTransactionAttributeSource(source);
        return interceptor;
    }

    @Bean
    public Advisor transactionAdviceAdvisor(@Autowired

                                            TransactionInterceptor interceptor){
        String expression="execution(public * com.maker.jdbc..service..*.*(..))";
        AspectJExpressionPointcut pointcut=new AspectJExpressionPointcut();
        pointcut.setExpression(expression);
        return new DefaultPointcutAdvisor(pointcut,interceptor);
    }
}
