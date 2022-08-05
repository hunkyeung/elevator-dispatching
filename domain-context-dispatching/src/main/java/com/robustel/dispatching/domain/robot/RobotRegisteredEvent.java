package com.robustel.dispatching.domain.robot;

import com.robustel.ddd.core.AbstractEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author YangXuehong
 * @date 2022/4/14
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class RobotRegisteredEvent extends AbstractEvent {
    private final Long robotId;
    private final String modelId;

    public RobotRegisteredEvent(Long robotId, String modelId) {
        super();
        this.robotId = robotId;
        this.modelId = modelId;
    }
}
