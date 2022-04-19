package com.robustel.dispatching.domain.requesthistory;

import com.robustel.dispatching.domain.elevator.ElevatorId;
import com.robustel.dispatching.domain.elevator.Request;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.yeung.api.AbstractEntity;

import java.time.Instant;

/**
 * @author YangXuehong
 * @date 2022/4/14
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class RequestHistory extends AbstractEntity<RequestHistoryId> {
    private final Request request;
    private final ElevatorId elevatorId;
    private final Instant finishedOn;

    public RequestHistory(RequestHistoryId id, Request request, ElevatorId elevatorId, Instant finishedOn) {
        super(id);
        this.request = request;
        this.elevatorId = elevatorId;
        this.finishedOn = finishedOn;
    }

    public static RequestHistory of(RequestHistoryId id, @NonNull Request request, @NonNull ElevatorId elevatorId) {
        return new RequestHistory(id, request, elevatorId, Instant.now());
    }
}
