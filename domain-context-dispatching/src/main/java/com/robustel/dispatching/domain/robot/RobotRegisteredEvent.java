package com.robustel.dispatching.domain.robot;

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
public class RobotRegisteredEvent extends AbstractEvent {
    private Long robotId;
    private String modelId;

    public RobotRegisteredEvent(Long robotId, String modelId) {
        super();
        this.robotId = robotId;
        this.modelId = modelId;
    }
}
