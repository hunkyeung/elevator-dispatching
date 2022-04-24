package com.robustel.adapter.persistence.mongodb.core;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YangXuehong
 * @date 2021/8/12
 */
@ConditionalOnProperty(name = "robustel.mongodb.transactionEnabled", havingValue = "true")
@Configuration
public class MongoTransactionConfig {

    /**
     * 配置方法过期时间，默认-1,永不超时
     */
    private static final int METHOD_TIME_OUT = 5000;

    /**
     * 配置切入点表达式
     */
    private static final String POINTCUT_EXPRESSION = "execution(public * com.robustel.*.application..*Application.*(..))";

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @Bean
    public TransactionInterceptor txAdvice(TransactionManager transactionManager) {
        /*事务管理规则，声明具备事务管理的方法名**/
        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        /*只读事务，不做更新操作*/
        RuleBasedTransactionAttribute readOnly = new RuleBasedTransactionAttribute();
        readOnly.setReadOnly(true);
        readOnly.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);
        /*当前存在事务就使用当前事务，当前不存在事务就创建一个新的事务*/
        RuleBasedTransactionAttribute required = new RuleBasedTransactionAttribute();
        /*抛出异常后执行切点回滚,这边你可以更换异常的类型*/
        required.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        /*PROPAGATION_REQUIRED:事务隔离性为1，若当前存在事务，则加入该事务；如果当前没有事务，则创建一个新的事务。这是默认值*/
        required.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        /*设置事务失效时间，如果超过5秒，则回滚事务*/
        required.setTimeout(METHOD_TIME_OUT);
        Map<String, TransactionAttribute> attributesMap = new HashMap<>(30);
        //设置增删改上传等使用事务
        attributesMap.put("do*", required);

        //查询开启只读
        attributesMap.put("get*", readOnly);

        source.setNameMap(attributesMap);
        return new TransactionInterceptor(transactionManager, source);
    }

    /**
     * 设置切面=切点pointcut+通知TxAdvice
     */
    @Bean
    public Advisor txAdviceAdvisor(TransactionManager transactionManager) {
        /* 声明切点的面：切面就是通知和切入点的结合。通知和切入点共同定义了关于切面的全部内容——它的功能、在何时和何地完成其功能*/
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        /*声明和设置需要拦截的方法,用切点语言描写*/
        pointcut.setExpression(POINTCUT_EXPRESSION);
        /*设置切面=切点pointcut+通知TxAdvice*/
        return new DefaultPointcutAdvisor(pointcut, txAdvice(transactionManager));
    }
}