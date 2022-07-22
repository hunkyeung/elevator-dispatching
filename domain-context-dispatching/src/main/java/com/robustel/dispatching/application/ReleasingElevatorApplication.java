package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorNotFoundException;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import org.springframework.stereotype.Service;

@Service
public class ReleasingElevatorApplication {
    private final ElevatorRepository elevatorRepository;

    public ReleasingElevatorApplication(ElevatorRepository elevatorRepository) {
        this.elevatorRepository = elevatorRepository;
    }

    public void doReleaseElevator(Long elevatorId) {
        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new ElevatorNotFoundException(elevatorId)
        );
        elevator.release();
        elevatorRepository.save(elevator);
    }
}
