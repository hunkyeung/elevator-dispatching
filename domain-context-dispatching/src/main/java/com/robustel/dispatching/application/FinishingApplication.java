package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorNotFoundException;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Passenger;
import com.robustel.dispatching.domain.requesthistory.RequestHistory;
import com.robustel.dispatching.domain.requesthistory.RequestHistoryRepository;
import lombok.Data;
import lombok.ToString;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new ElevatorNotFoundException(elevatorId)
        );
        RequestHistory requestHistory = elevator.finish(command.passenger);
        if (!Objects.isNull(requestHistory)) {
            requestHistoryRepository.save(requestHistory);
        }
        elevatorRepository.save(elevator);
    }

    @Data
    @ToString
    public static class Command {
        private Passenger passenger;
    }
}
