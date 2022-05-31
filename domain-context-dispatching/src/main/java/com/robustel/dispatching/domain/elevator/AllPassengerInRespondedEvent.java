package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.AbstractEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author YangXuehong
 * @date 2022/4/19
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public class AllPassengerInRespondedEvent extends AbstractEvent {
    private final Long elevatorId;

    public AllPassengerInRespondedEvent(Long elevatorId) {
        this.elevatorId = elevatorId;
    }
}
