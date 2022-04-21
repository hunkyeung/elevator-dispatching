package com.robustel.dispatching.domain.robot;

import com.robustel.dispatching.domain.elevator.ElevatorId;
import org.yeung.api.DomainException;

/**
 * @author YangXuehong
 * @date 2022/4/21
 */
public class RobotNotAllowedEnterException extends DomainException {
    public RobotNotAllowedEnterException(RobotId robotId, ElevatorId elevatorId) {
        super("Robot[" + robotId.toString() + "] is not allowed to enter the elevator[" + elevatorId.toString() + "]. ");
    }
}
