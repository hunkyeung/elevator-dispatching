package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.AbstractEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
@EqualsAndHashCode(callSuper = true)
public class ElevatorCompletedInEvent extends AbstractEvent {
    private Elevator elevator;

    public ElevatorCompletedInEvent(Elevator elevator) {
        this.elevator = elevator;
    }
}

