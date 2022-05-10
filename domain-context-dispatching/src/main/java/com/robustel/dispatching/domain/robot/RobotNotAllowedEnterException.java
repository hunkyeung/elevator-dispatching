package com.robustel.dispatching.domain.robot;

import com.robustel.ddd.core.DomainException;
import com.robustel.dispatching.domain.elevator.ElevatorId;

/**
 * @author YangXuehong
 * @date 2022/4/21
 */
public class RobotNotAllowedEnterException extends DomainException {
    public RobotNotAllowedEnterException(RobotId robotId, ElevatorId elevatorId) {
        super("未找不到该机器人【" + robotId + "】与电梯【" + elevatorId + "】绑定关系");
    }
}
