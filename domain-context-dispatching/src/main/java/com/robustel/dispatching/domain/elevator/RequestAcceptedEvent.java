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
public class RequestAcceptedEvent extends AbstractEvent {
    private final Long elevatorId;
    private final Request request;

    public RequestAcceptedEvent(Long elevatorId, Request request) {
        this.elevatorId = elevatorId;
        this.request = request;
    }
}
