package com.robustel.dispatching.domain.elevator;

import com.robustel.dispatching.domain.robot.RobotId;
import lombok.ToString;
import org.yeung.api.DomainException;

/**
 * @author YangXuehong
 * @date 2022/4/19
 */
@ToString(callSuper = true)
public class RequestAlreadyExistException extends DomainException {
    public RequestAlreadyExistException(RobotId robotId, ElevatorId elevatorId) {
        super("已经存在该机器人【" + robotId + "】搭乘电梯【" + elevatorId + "】请求");
    }
}
