package com.robustel.dispatching.domain.takingrequesthistory;

import com.robustel.ddd.core.AbstractEntity;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.ddd.service.UidGenerator;
import com.robustel.dispatching.domain.elevator.TakingRequest;
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
public class TakingRequestHistory extends AbstractEntity<Long> {
    private TakingRequest takingRequest;
    private Long elevatorId;
    private Instant archivedOn;

    public TakingRequestHistory(Long id, TakingRequest takingRequest, Long elevatorId, Instant archivedOn) {
        super(id);
        this.takingRequest = takingRequest;
        this.elevatorId = elevatorId;
        this.archivedOn = archivedOn;
    }

    public static TakingRequestHistory create(@NonNull TakingRequest takingRequest, @NonNull Long elevatorId) {
        return new TakingRequestHistory(ServiceLocator.service(UidGenerator.class).nextId(), takingRequest, elevatorId, Instant.now());
    }
}
