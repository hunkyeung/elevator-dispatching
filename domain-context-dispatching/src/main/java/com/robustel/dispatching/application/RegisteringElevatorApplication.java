package com.robustel.dispatching.application;

import com.robustel.ddd.service.EventPublisher;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRegisteredEvent;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
        Elevator elevator = Elevator.create(command.getId(), command.getName(),
                command.getHighest(), command.getLowest());
        elevatorRepository.save(elevator);
        ServiceLocator.service(EventPublisher.class).publish(new ElevatorRegisteredEvent(elevator.id(), command.modelId, command.sn));
        return elevator.id();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Command {
        private long id;
        private String name;
        private int highest;
        private int lowest;
        private String modelId;
        private String sn;
    }
}
