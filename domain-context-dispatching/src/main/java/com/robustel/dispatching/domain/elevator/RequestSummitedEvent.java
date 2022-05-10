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
public class RequestSummitedEvent extends AbstractEvent {
    private final ElevatorId elevatorId;
    private final Request request;

    public RequestSummitedEvent(ElevatorId elevatorId, Request request) {
        this.elevatorId = elevatorId;
        this.request = request;
    }
}
