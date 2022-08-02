package com.robustel.dispatching.domain;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.elevator.Passenger;

import java.util.Optional;

/**
 * @author YangXuehong
 * @date 2022/4/8
 */
public interface SelectingElevatorStrategyService {
    Optional<Elevator> selectElevator(Passenger passenger, Floor from, Floor to);

}
