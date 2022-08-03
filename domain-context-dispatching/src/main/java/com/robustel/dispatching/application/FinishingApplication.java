package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Passenger;
import com.robustel.dispatching.domain.requesthistory.RequestHistoryRepository;
import org.springframework.stereotype.Service;

/**
 * @author YangXuehong
 * @date 2022/4/11
 */
@Service
public class FinishingApplication {
    private final ElevatorRepository elevatorRepository;
    private final RequestHistoryRepository requestHistoryRepository;

    public FinishingApplication(ElevatorRepository elevatorRepository, RequestHistoryRepository requestHistoryRepository) {
        this.elevatorRepository = elevatorRepository;
        this.requestHistoryRepository = requestHistoryRepository;
    }

    public void doFinish(Long elevatorId, Command command) {
        var elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new Elevator.ElevatorNotFoundException(elevatorId)
        );
        var optionalRequestHistory = elevator.finish(command.passenger);
        optionalRequestHistory.ifPresent(requestHistoryRepository::save);
        elevatorRepository.save(elevator);
    }

    public record Command(Passenger passenger) {
    }

}
