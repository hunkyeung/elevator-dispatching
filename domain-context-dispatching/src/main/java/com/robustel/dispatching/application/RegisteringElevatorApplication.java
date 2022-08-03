package com.robustel.dispatching.application;

import com.robustel.ddd.service.EventPublisher;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRegisteredEvent;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import org.springframework.stereotype.Service;

/**
 * @author YangXuehong
 * @date 2022/4/11
 */
@Service
public class RegisteringElevatorApplication {
    private final ElevatorRepository elevatorRepository;

    public RegisteringElevatorApplication(ElevatorRepository elevatorRepository) {
        this.elevatorRepository = elevatorRepository;
    }

    public Long doRegister(Command command) {
        var elevator = Elevator.create(command.id, command.name,
                command.highest, command.lowest);
        elevatorRepository.save(elevator);
        ServiceLocator.service(EventPublisher.class).publish(new ElevatorRegisteredEvent(elevator.id(), command.modelId, command.sn));
        return elevator.id();
    }

    public record Command(long id, String name, int highest, int lowest, String modelId, String sn) {
    }

}
