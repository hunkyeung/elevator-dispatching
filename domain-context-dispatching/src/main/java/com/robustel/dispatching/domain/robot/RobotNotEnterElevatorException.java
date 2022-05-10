package com.robustel.dispatching.domain.robot;

import com.robustel.ddd.core.DomainException;
import com.robustel.dispatching.domain.elevator.ElevatorId;

/**
 * @author YangXuehong
 * @date 2022/4/22
 */
public class RobotNotEnterElevatorException extends DomainException {
    public RobotNotEnterElevatorException(RobotId robotId, ElevatorId elevatorId) {
        super("电梯人【" + robotId + "】还未进入该电梯【" + elevatorId + "】");
    }
}
