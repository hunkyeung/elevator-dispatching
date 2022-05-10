package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.DomainException;
import lombok.ToString;

/**
 * @author YangXuehong
 * @date 2022/4/19
 */
@ToString(callSuper = true)
public class ElevatorNotFoundException extends DomainException {

    public ElevatorNotFoundException(ElevatorId elevatorId) {
        super("找不到该电梯【" + elevatorId + "】");
    }
}
