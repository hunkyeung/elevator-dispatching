package com.robustel.dispatching.domain.robot;

import lombok.ToString;
import org.yeung.api.AbstractIdentity;

/**
 * @author YangXuehong
 * @date 2022/4/8
 */
@ToString(callSuper = true)
public class RobotId extends AbstractIdentity<String> {
    protected RobotId(String value) {
        super(value);
    }

    public static RobotId of(String value) {
        return new RobotId(value);
    }
}
