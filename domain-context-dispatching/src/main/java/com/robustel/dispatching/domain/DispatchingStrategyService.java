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
            super("未找到合适的电梯给机器人【" + robotId + "】，请确保该机器人已经绑定过电梯，且绑定的电梯处于可用状态");
        }
    }
}
