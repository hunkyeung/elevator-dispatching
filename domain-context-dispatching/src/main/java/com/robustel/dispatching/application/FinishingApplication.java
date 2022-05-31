package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorNotFoundException;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Passenger;
import com.robustel.dispatching.domain.takingrequesthistory.TakingRequestHistory;
import com.robustel.dispatching.domain.takingrequesthistory.TakingRequestHistoryRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private final TakingRequestHistoryRepository takingRequestHistoryRepository;

    public FinishingApplication(ElevatorRepository elevatorRepository, TakingRequestHistoryRepository takingRequestHistoryRepository) {
        this.elevatorRepository = elevatorRepository;
        this.takingRequestHistoryRepository = takingRequestHistoryRepository;
    }

    public void doFinish(Long elevatorId, Command command) {
        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new ElevatorNotFoundException(elevatorId)
        );
        TakingRequestHistory takingRequestHistory = elevator.finish(command.getPassenger());
        if (!Objects.isNull(takingRequestHistory)) {
            takingRequestHistoryRepository.save(takingRequestHistory);
        }
        elevatorRepository.save(elevator);
    }

    @ToString
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Command {
        private Passenger passenger;
    }
}
