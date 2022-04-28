package com.robustel.adapter.common;

import com.robustel.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.yeung.api.DomainException;

/**
 * @author YangXuehong
 * @date 2021/8/20
 */
@Component
@Aspect
@Order(150)
@Slf4j
public class VerifyingUser {
    @Value("${robustel.security-key}")
    private String securityKey;

    @Pointcut("(execution(public * com.robustel.*.application..*Application.*(..))) " +
            "&& !(execution(public * com.robustel.thing.application.GettingThingIdByNameApplication.*(..)))" +
            "&& !@annotation(com.robustel.utils.Client)")
    public void verifyingUser() {
        throw new UnsupportedOperationException();
    }

    @Before("verifyingUser()")
    public void before() {
        verify();
    }

    private void verify() {
        String securityKey = ThreadLocalUtil.get("SECURITY-KEY");
        if (StringUtils.isBlank(securityKey) || !securityKey.equals(this.securityKey)) {
            throw new DomainException("请携带安全密钥访问。如没有，请向管理员索要");
        }
    }
}

