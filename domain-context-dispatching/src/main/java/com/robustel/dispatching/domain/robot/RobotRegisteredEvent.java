package com.robustel.dispatching.domain.robot;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.yeung.api.AbstractEvent;

/**
 * @author YangXuehong
 * @date 2022/4/14
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RobotRegisteredEvent extends AbstractEvent {
    private final RobotId robotId;
    private final String modelId;

    public RobotRegisteredEvent(RobotId robotId, String modelId) {
        this.robotId = robotId;
        this.modelId = modelId;
    }
}
