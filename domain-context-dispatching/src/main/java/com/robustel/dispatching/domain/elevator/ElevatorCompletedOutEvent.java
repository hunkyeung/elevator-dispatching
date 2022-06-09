package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.AbstractEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public class ElevatorCompletedOutEvent extends AbstractEvent {
    private final Elevator elevator;

    public ElevatorCompletedOutEvent(Elevator elevator) {
        this.elevator = elevator;
    }
}
