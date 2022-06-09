package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorNotFoundException;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Passenger;
import org.springframework.stereotype.Service;

/**
 * @author YangXuehong
 * @date 2022/4/21
 */
@Service
public class BindingAndUnbindingRobotApplication {
    private final ElevatorRepository elevatorRepository;

    public BindingAndUnbindingRobotApplication(ElevatorRepository elevatorRepository) {
        this.elevatorRepository = elevatorRepository;
    }

    public void doBind(Long elevatorId, String robotId) {
        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new ElevatorNotFoundException(elevatorId)
        );
        elevator.bind(Passenger.of(robotId));
        elevatorRepository.save(elevator);
    }

    public void doUnbind(Long elevatorId, String robotId) {
        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new ElevatorNotFoundException(elevatorId)
        );
        elevator.unbind(Passenger.of(robotId));
        elevatorRepository.save(elevator);
    }
}
