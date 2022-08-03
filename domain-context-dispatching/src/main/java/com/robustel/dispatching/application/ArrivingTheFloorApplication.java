package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.elevator.Direction;
import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Floor;
import org.springframework.stereotype.Service;

/**
 * @author YangXuehong
 * @date 2022/4/13
 */
@Service
public class ArrivingTheFloorApplication {
    private final ElevatorRepository elevatorRepository;

    public ArrivingTheFloorApplication(ElevatorRepository elevatorRepository) {
        this.elevatorRepository = elevatorRepository;
    }

    public void doArrive(Long elevatorId, Floor floor, Direction nextDirection) {
        var elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new Elevator.ElevatorNotFoundException(elevatorId)
        );
        elevator.arrive(floor, nextDirection);
        elevatorRepository.save(elevator);
    }
}
