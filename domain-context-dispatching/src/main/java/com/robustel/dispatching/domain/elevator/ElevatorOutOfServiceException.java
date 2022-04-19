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
        super("Elevator[" + elevatorId.toString() + "] is out of service. ");
    }
}
