package com.robustel.dispatching.domain.robot;

import com.robustel.ddd.core.DomainException;
import lombok.ToString;

/**
 * @author YangXuehong
 * @date 2022/4/19
 */
@ToString(callSuper = true)
public class RobotNotFoundException extends DomainException {
    public RobotNotFoundException(RobotId robotId) {
        super("找不到该机器人【" + robotId + "】");
    }
}
