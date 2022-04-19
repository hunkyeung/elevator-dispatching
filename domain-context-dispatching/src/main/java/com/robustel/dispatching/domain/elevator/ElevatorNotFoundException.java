package com.robustel.dispatching.domain.elevator;

import lombok.ToString;
import org.yeung.api.DomainException;

/**
 * @author YangXuehong
 * @date 2022/4/19
 */
@ToString(callSuper = true)
public class ElevatorNotFoundException extends DomainException {

    public ElevatorNotFoundException(ElevatorId elevatorId) {
        super("Elevator[" + elevatorId.toString() + "] could not be found. ");
    }
}
