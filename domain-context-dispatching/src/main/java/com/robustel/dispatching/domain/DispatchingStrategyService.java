package com.robustel.dispatching.domain;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.robot.Robot;
import com.robustel.dispatching.domain.robot.RobotId;
import org.yeung.api.DomainException;

/**
 * @author YangXuehong
 * @date 2022/4/8
 */
public interface DispatchingStrategyService {
    Elevator selectElevator(Robot robot, Floor from, Floor to);

    class NoElevatorAvailableException extends DomainException {
        public NoElevatorAvailableException(RobotId robotId) {
            super("No elevator available for the robot[" + robotId.toString() + "]. ");
        }
    }
}
