package com.robustel.dispatching.domain.elevator;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.yeung.api.AbstractEvent;

/**
 * @author YangXuehong
 * @date 2022/4/19
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public class ElevatorDoorReleasedEvent extends AbstractEvent {
    private final ElevatorId elevatorId;

    public ElevatorDoorReleasedEvent(ElevatorId elevatorId) {
        this.elevatorId = elevatorId;
    }
}
