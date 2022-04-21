package com.robustel.dispatching.domain.robot;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.yeung.api.AbstractEvent;

import java.util.Map;

/**
 * @author YangXuehong
 * @date 2022/4/14
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RobotRegisteredEvent extends AbstractEvent {
    private final RobotId robotId;
    private final Map<String, Object> params;

    public RobotRegisteredEvent(RobotId robotId, Map<String, Object> params) {
        super();
        this.robotId = robotId;
        this.params = params;
    }
}
