package com.robustel.dispatching.domain.robot;

import com.robustel.ddd.core.DomainException;
import lombok.ToString;

/**
 * @author YangXuehong
 * @date 2022/4/19
 */
@ToString(callSuper = true)
public class RobotNotFoundException extends DomainException {
    public RobotNotFoundException(Long robotId) {
        super(String.format("找不到该机器人【%s】", robotId));
    }
}
