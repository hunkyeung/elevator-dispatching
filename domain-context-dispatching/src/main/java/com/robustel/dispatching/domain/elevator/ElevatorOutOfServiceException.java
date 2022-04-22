package com.robustel.dispatching.domain.elevator;

import lombok.ToString;
import org.yeung.api.DomainException;

/**
 * @author YangXuehong
 * @date 2022/4/19
 */
@ToString(callSuper = true)
public class ElevatorOutOfServiceException extends DomainException {

    public ElevatorOutOfServiceException(ElevatorId elevatorId) {
        super("该电梯【" + elevatorId + "】不可用");
    }
}
