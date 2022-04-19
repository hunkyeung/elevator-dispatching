package com.robustel.dispatching.domain;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.robot.Robot;

/**
 * @author YangXuehong
 * @date 2022/4/8
 */
public interface DispatchingStrategyService {
    Elevator selectElevator(Robot robot, Floor from, Floor to);
}
