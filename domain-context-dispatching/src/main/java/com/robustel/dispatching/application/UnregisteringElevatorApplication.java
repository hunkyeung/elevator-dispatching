package com.robustel.dispatching.application;

import com.robustel.ddd.service.EventPublisher;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.ElevatorUnregisteredEvent;
import org.springframework.stereotype.Service;

/**
 * @author YangXuehong
 * @date 2022/4/11
 */
@Service
public class UnregisteringElevatorApplication {
    private final ElevatorRepository elevatorRepository;

    public UnregisteringElevatorApplication(ElevatorRepository elevatorRepository) {
        this.elevatorRepository = elevatorRepository;
    }

    public void doUnregister(long elevatorId) {
        elevatorRepository.deleteById(elevatorId);
        ServiceLocator.service(EventPublisher.class).publish(new ElevatorUnregisteredEvent(elevatorId));
    }
}
