package com.robustel.dispatching.domain.elevator;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.yeung.api.AbstractEvent;

/**
 * @author YangXuehong
 * @date 2022/4/14
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RobotLeftEvent extends AbstractEvent {
    private final ElevatorId elevatorId;
    private final Request request;

    public RobotLeftEvent(ElevatorId elevatorId, Request request) {
        this.elevatorId = elevatorId;
        this.request = request;
    }
}
