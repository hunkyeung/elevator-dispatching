package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.AbstractEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author YangXuehong
 * @date 2022/4/14
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public class ElevatorArrivedEvent extends AbstractEvent {
    private final Long robotId;
    private final boolean enterOrLeave; // true -> enter;false -> leave

    public ElevatorArrivedEvent(Long robotId, boolean enterOrLeave) {
        this.robotId = robotId;
        this.enterOrLeave = enterOrLeave;
    }
}
