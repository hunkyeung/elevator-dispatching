package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.AbstractEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
@EqualsAndHashCode(callSuper = true)
public class ReleaseDoorEvent extends AbstractEvent {
    private final Long elevatorId;

    public ReleaseDoorEvent(Long elevatorId) {
        this.elevatorId = elevatorId;
    }
}
