package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.elevator.*;
import org.springframework.stereotype.Service;

/**
 * @author YangXuehong
 * @date 2022/4/13
 */
@Service
public class ArrivingFloorApplication {
    private final ElevatorRepository elevatorRepository;

    public ArrivingFloorApplication(ElevatorRepository elevatorRepository) {
        this.elevatorRepository = elevatorRepository;
    }

    public void doArrive(ElevatorId elevatorId, Floor floor) {
        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new ElevatorNotFoundException(elevatorId)
        );
        elevator.arrive(floor);
        elevatorRepository.save(elevator);
    }
}
