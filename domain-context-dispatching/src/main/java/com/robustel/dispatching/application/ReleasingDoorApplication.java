package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorId;
import com.robustel.dispatching.domain.elevator.ElevatorNotFoundException;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import org.springframework.stereotype.Service;

/**
 * @author YangXuehong
 * @date 2022/4/19
 */
@Service
public class ReleasingDoorApplication {
    private final ElevatorRepository elevatorRepository;

    public ReleasingDoorApplication(ElevatorRepository elevatorRepository) {
        this.elevatorRepository = elevatorRepository;
    }

    public void doReleaseDoor(ElevatorId elevatorId) {
        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new ElevatorNotFoundException(elevatorId)
        );
        elevator.release();
        elevatorRepository.save(elevator);
    }
}
