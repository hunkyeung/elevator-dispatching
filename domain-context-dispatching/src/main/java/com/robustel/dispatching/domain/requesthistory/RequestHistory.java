package com.robustel.dispatching.domain.requesthistory;

import com.robustel.ddd.core.AbstractEntity;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.ddd.service.UidGenerator;
import com.robustel.dispatching.domain.elevator.Request;
import lombok.*;

import java.time.Instant;

/**
 * @author YangXuehong
 * @date 2022/4/14
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RequestHistory extends AbstractEntity<Long> {
    private Request request;
    private Long elevatorId;
    private Instant archivedOn;

    public RequestHistory(Long id, Request request, Long elevatorId, Instant archivedOn) {
        super(id);
        this.request = request;
        this.elevatorId = elevatorId;
        this.archivedOn = archivedOn;
    }

    public static RequestHistory create(@NonNull Request request, @NonNull Long elevatorId) {
        return new RequestHistory(ServiceLocator.service(UidGenerator.class).nextId(), request, elevatorId, Instant.now());
    }
}
