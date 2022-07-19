package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import lombok.Getter;
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
                command.getHighest(), command.getLowest(), command.getModelId(), command.getSn());
        elevatorRepository.save(elevator);
        return elevator.id();
    }

    @Getter
    public static class Command {
        private long id;
        private String name;
        private int highest;
        private int lowest;
        private String modelId;
        private String sn;
    }
}
