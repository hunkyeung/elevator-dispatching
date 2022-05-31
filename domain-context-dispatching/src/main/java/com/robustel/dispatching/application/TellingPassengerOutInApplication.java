package com.robustel.dispatching.application;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.domain.elevator.*;
import org.springframework.stereotype.Service;

/**
 * @author YangXuehong
 * @date 2022/4/13
 */
@Service
public class TellingPassengerOutInApplication {
    private final ElevatorRepository elevatorRepository;

    public TellingPassengerOutInApplication(ElevatorRepository elevatorRepository) {
        this.elevatorRepository = elevatorRepository;
    }

    public void doTellPassengerOutIn(Long elevatorId) {
        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new ElevatorNotFoundException(elevatorId)
        );
        elevator.passengerOutIn();
        elevatorRepository.save(elevator);
    }

    public void doArrive(Long elevatorId, Floor floor) {
        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new ElevatorNotFoundException(elevatorId)
        );
        elevator.arrive(floor);
        elevator.passengerOutIn();
        elevatorRepository.save(elevator);
    }

    @Subscribe
    public void listenOn(AllPassengerOutRespondedEvent event) {
        Elevator elevator = elevatorRepository.findById(event.getElevatorId()).orElseThrow(
                () -> new ElevatorNotFoundException(event.getElevatorId())
        );
        elevator.tellIn();
        elevatorRepository.save(elevator);
    }
}
