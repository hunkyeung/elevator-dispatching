package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.AbstractIdentity;

/**
 * @author YangXuehong
 * @date 2022/4/8
 */
public class ElevatorId extends AbstractIdentity<String> {
    protected ElevatorId(String value) {
        super(value);
    }

    public static ElevatorId of(String value) {
        return new ElevatorId(value);
    }
}
