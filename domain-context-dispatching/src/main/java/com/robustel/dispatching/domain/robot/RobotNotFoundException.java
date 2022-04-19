package com.robustel.dispatching.domain.robot;

import lombok.ToString;
import org.yeung.api.DomainException;

/**
 * @author YangXuehong
 * @date 2022/4/19
 */
@ToString(callSuper = true)
public class RobotNotFoundException extends DomainException {
    public RobotNotFoundException(RobotId robotId) {
        super("Robot[" + robotId.toString() + "] could not be found. ");
    }
}
