package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.AbstractEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
@Getter
public class ElevatorUnregisteredEvent extends AbstractEvent {
    private final long elevatorId;

    public ElevatorUnregisteredEvent(long elevatorId) {
        this.elevatorId = elevatorId;
    }
}
