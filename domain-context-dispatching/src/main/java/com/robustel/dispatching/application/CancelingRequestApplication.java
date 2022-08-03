package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Passenger;
import com.robustel.dispatching.domain.requesthistory.RequestHistoryRepository;
import org.springframework.stereotype.Service;

/**
 * @author YangXuehong
 * @date 2022/4/19
 */
@Service
public class CancelingRequestApplication {
    private final ElevatorRepository elevatorRepository;
    private final RequestHistoryRepository requestHistoryRepository;

    public CancelingRequestApplication(ElevatorRepository elevatorRepository, RequestHistoryRepository requestHistoryRepository) {
        this.elevatorRepository = elevatorRepository;
        this.requestHistoryRepository = requestHistoryRepository;
    }

    public void doCancelRequest(Long elevatorId, Command command) {
        var elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new Elevator.ElevatorNotFoundException(elevatorId)
        );
        var requestHistory = elevator.cancelRequest(command.passenger, command.cause);
        requestHistoryRepository.save(requestHistory);
        elevatorRepository.save(elevator);
    }

    public record Command(Passenger passenger, String cause) {

    }
}
