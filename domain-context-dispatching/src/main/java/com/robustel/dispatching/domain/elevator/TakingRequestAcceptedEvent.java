package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.AbstractEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author YangXuehong
 * @date 2022/4/14
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TakingRequestAcceptedEvent extends AbstractEvent {
    private final Long elevatorId;
    private final TakingRequest takingRequest;

    public TakingRequestAcceptedEvent(Long elevatorId, TakingRequest takingRequest) {
        this.elevatorId = elevatorId;
        this.takingRequest = takingRequest;
    }
}
