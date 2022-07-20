package com.robustel.dispatching.domain.elevator;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ElevatorUnregisteredEvent {
    private final long elevatorId;

    public ElevatorUnregisteredEvent(long elevatorId) {
        this.elevatorId = elevatorId;
    }
}
