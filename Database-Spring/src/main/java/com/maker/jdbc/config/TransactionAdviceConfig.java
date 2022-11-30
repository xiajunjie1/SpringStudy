package com.maker.jdbc.config;

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

@Configuration
@Aspect //AOP代理配置
public class TransactionAdviceConfig {
    /**
     * 事务切面是通过TransactionInterceptor拦截器完成的
     * 所以需要注册一个拦截器
     * */
    @Bean("txAdvice2")
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
        //创建名称匹配事务
        NameMatchTransactionAttributeSource source=new NameMatchTransactionAttributeSource();
        source.setNameMap(mapAttribute);
        TransactionInterceptor interceptor=new TransactionInterceptor();
        interceptor.setTransactionManager(transactionManager);
        interceptor.setTransactionAttributeSource(source);
        return interceptor;

    }
    /**
     * 切面配置，将增强织入到切点中
     * */
    @Bean
    public Advisor TransactionAdviceAdvisor(@Autowired
                                                @Qualifier("txAdvice2")
                                                TransactionInterceptor interceptor){
        String expression="execution(public * com.maker.jdbc..service..*.*(..))";
        AspectJExpressionPointcut pointcut=new AspectJExpressionPointcut();
        pointcut.setExpression(expression);
        return new DefaultPointcutAdvisor(pointcut,interceptor);
    }
}
