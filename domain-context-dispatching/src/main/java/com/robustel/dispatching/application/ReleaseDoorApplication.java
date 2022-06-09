package com.robustel.dispatching.application;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorCompletedInEvent;
import com.robustel.dispatching.domain.elevator.ElevatorNotFoundException;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import org.springframework.stereotype.Service;

@Service
public class ReleaseDoorApplication {
    private final ElevatorRepository elevatorRepository;

    public ReleaseDoorApplication(ElevatorRepository elevatorRepository) {
        this.elevatorRepository = elevatorRepository;
    }

    public void doReleaseDoor(Long elevatorId) {
        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new ElevatorNotFoundException(elevatorId)
        );
        elevator.releaseDoor();
        elevatorRepository.save(elevator);
    }

    @Subscribe
    public void listenOn(ElevatorCompletedInEvent event) {
        event.getElevator().releaseDoor();
    }
}
