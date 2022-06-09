package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.AbstractEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public class ElevatorDoorOpenedEvent extends AbstractEvent {
    private Elevator elevator;

    public ElevatorDoorOpenedEvent(Elevator elevator) {
        this.elevator = elevator;
    }
}
