package com.robustel.adapter.common;

import com.robustel.adapter.persistence.mongodb.Tenant;
import com.robustel.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author YangXuehong
 * @date 2021/8/20
 */
@Component
@Aspect
@Order(5)
@Slf4j
public class FillingDefaultTenant {

    @Pointcut("(execution(public * com.robustel.*.application..*Application.*(..))) " +
            "&& !(execution(public * com.robustel.thing.application.GettingThingIdByNameApplication.*(..)))" +
            "&& !@annotation(com.robustel.utils.Client)")
    public void fillingTenant() {
        throw new UnsupportedOperationException();
    }

    @Before("fillingTenant()")
    public void before() {
        ThreadLocalUtil.set(Tenant.TENANT_OF_THREAD_LOCAL, "CHINA");
    }
}

