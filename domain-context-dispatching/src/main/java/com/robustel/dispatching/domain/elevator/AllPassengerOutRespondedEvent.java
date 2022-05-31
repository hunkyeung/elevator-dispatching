package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.AbstractEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AllPassengerOutRespondedEvent extends AbstractEvent {

    private final Long elevatorId;

    public AllPassengerOutRespondedEvent(Long elevatorId) {
        this.elevatorId = elevatorId;
    }
}
