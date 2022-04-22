package com.robustel.dispatching.domain.robot;

import com.robustel.dispatching.domain.elevator.ElevatorId;
import org.yeung.api.DomainException;

/**
 * @author YangXuehong
 * @date 2022/4/22
 */
public class RobotNotEnterElevatorException extends DomainException {
    public RobotNotEnterElevatorException(RobotId robotId, ElevatorId elevatorId) {
        super("电梯人【" + robotId + "】还未进入该电梯【" + elevatorId + "】");
    }
}
