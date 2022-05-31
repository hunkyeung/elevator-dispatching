package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.AbstractEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author YangXuehong
 * @date 2022/4/13
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public class ElevatorRegisteredEvent extends AbstractEvent {
    private final Long elevatorId;
    private final String modelId;
    private final String sn;

    public ElevatorRegisteredEvent(Long elevatorId, String modelId, String sn) {
        super();
        this.elevatorId = elevatorId;
        this.modelId = modelId;
        this.sn = sn;
    }
}
