package com.robustel.dispatching.domain.elevator;

import com.robustel.dispatching.domain.robot.RobotId;
import lombok.ToString;
import org.yeung.api.DomainException;

/**
 * @author YangXuehong
 * @date 2022/4/19
 */
@ToString(callSuper = true)
public class RequestNotFoundException extends DomainException {
    public RequestNotFoundException(RobotId robotId, ElevatorId elevatorId) {
        super("Request that robot[" + robotId.toString() + "] takes the elevator[" + elevatorId.toString() + "] could not be found. ");
    }
}
