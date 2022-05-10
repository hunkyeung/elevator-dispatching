package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.DomainException;
import com.robustel.dispatching.domain.robot.RobotId;
import lombok.ToString;

/**
 * @author YangXuehong
 * @date 2022/4/19
 */
@ToString(callSuper = true)
public class RequestNotFoundException extends DomainException {
    public RequestNotFoundException(RobotId robotId, ElevatorId elevatorId) {
        super("找不到该机器人【" + robotId + "】乘梯【" + elevatorId + "】请求");
    }
}
