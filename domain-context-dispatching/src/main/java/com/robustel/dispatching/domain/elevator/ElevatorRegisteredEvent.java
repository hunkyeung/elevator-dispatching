package com.robustel.dispatching.domain.elevator;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.yeung.api.AbstractEvent;

/**
 * @author YangXuehong
 * @date 2022/4/13
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public class ElevatorRegisteredEvent extends AbstractEvent {
    private final ElevatorId elevatorId;
    private final String modelId;

    public ElevatorRegisteredEvent(ElevatorId elevatorId, String modelId) {
        super();
        this.elevatorId = elevatorId;
        this.modelId = modelId;
    }
}
