package com.robustel.dispatching.domain.elevator;

import com.robustel.dispatching.domain.robot.RobotId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.yeung.api.AbstractEvent;

/**
 * @author YangXuehong
 * @date 2022/4/14
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public class ElevatorArrivedEvent extends AbstractEvent {
    private final RobotId robotId;
    private final boolean enterOrLeave; // true -> enter;false -> leave

    public ElevatorArrivedEvent(RobotId robotId, boolean enterOrLeave) {
        this.robotId = robotId;
        this.enterOrLeave = enterOrLeave;
    }
}
