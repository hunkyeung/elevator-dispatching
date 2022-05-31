package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.DomainException;
import com.robustel.dispatching.domain.elevator.Passenger;

/**
 * @author YangXuehong
 * @date 2022/4/21
 */
public class PassengerNotAllowedException extends DomainException {
    public PassengerNotAllowedException(Passenger passenger, Long elevatorId) {
        super(String.format("未找不到该乘客【%s】与电梯【%s】绑定关系", passenger, elevatorId));
    }
}
