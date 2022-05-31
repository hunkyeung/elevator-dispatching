package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.DomainException;
import lombok.ToString;

/**
 * @author YangXuehong
 * @date 2022/4/19
 */
@ToString(callSuper = true)
public class TakingRequestNotFoundException extends DomainException {
    public TakingRequestNotFoundException(Passenger passenger, Long elevatorId) {
        super(String.format("找不到该乘客【%s】乘梯【%s】请求", passenger, elevatorId));
    }
}
