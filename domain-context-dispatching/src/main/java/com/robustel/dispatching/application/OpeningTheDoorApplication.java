package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.elevator.*;
import org.springframework.stereotype.Service;

/**
 * @author YangXuehong
 * @date 2022/4/13
 */
@Service
public class OpeningTheDoorApplication {
    private final ElevatorRepository elevatorRepository;

    public OpeningTheDoorApplication(ElevatorRepository elevatorRepository) {
        this.elevatorRepository = elevatorRepository;
    }

    public void doOpenDoor(Long elevatorId, Floor floor, Direction nextDirection) {
        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new ElevatorNotFoundException(elevatorId)
        );
        elevator.open(floor, nextDirection);
        elevatorRepository.save(elevator);
    }
}
