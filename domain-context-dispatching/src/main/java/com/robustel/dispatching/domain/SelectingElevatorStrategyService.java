package com.robustel.dispatching.domain;

import com.robustel.ddd.core.DomainException;
import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.elevator.Passenger;

/**
 * @author YangXuehong
 * @date 2022/4/8
 */
public interface SelectingElevatorStrategyService {
    Elevator selectElevator(Passenger passenger, Floor from, Floor to);

    class NoElevatorAvailableException extends DomainException {
        public NoElevatorAvailableException(Passenger passenger) {
            super(String.format("未找到合适的电梯给乘客【%s】，请确保该乘客已经绑定过电梯，且绑定的电梯处于可用状态", passenger.getId()));
        }
    }
}
