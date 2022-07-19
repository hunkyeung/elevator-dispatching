package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.AbstractEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class LightenEvent extends AbstractEvent {
    private final Long elevatorId;
    private final Floor floor;

    public LightenEvent(Long elevatorId, @NonNull Floor floor) {
        this.elevatorId = elevatorId;
        this.floor = floor;
    }
}
