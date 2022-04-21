package com.robustel.dispatching.domain.elevator;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.yeung.api.AbstractEvent;

import java.util.Map;

/**
 * @author YangXuehong
 * @date 2022/4/13
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public class ElevatorRegisteredEvent extends AbstractEvent {
    private final ElevatorId elevatorId;
    private final Map<String, Object> params;

    public ElevatorRegisteredEvent(ElevatorId elevatorId, Map<String, Object> params) {
        super();
        this.elevatorId = elevatorId;
        this.params = params;
    }
}
