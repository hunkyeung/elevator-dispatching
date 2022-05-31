package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.AbstractEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
@EqualsAndHashCode(callSuper = true)
public class NoPassengerEvent extends AbstractEvent {
    private final Long elevatorId;

    public NoPassengerEvent(Long elevatorId) {
        this.elevatorId = elevatorId;
    }
}
