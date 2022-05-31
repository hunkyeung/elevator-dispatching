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

/**
 * @author YangXuehong
 * @date 2022/4/19
 */
@Service
public class CancelingTakingRequestApplication {
    private final ElevatorRepository elevatorRepository;
    private final TakingRequestHistoryRepository takingRequestHistoryRepository;

    public CancelingTakingRequestApplication(ElevatorRepository elevatorRepository, TakingRequestHistoryRepository takingRequestHistoryRepository) {
        this.elevatorRepository = elevatorRepository;
        this.takingRequestHistoryRepository = takingRequestHistoryRepository;
    }

    public void doCancelTakingRequest(Long elevatorId, Command command) {
        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new ElevatorNotFoundException(elevatorId)
        );
        TakingRequestHistory takingRequestHistory = elevator.cancelTakingRequest(command.getPassenger(), command.getCause());
        takingRequestHistoryRepository.save(takingRequestHistory);
        elevatorRepository.save(elevator);
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Command {
        private Passenger passenger;
        private String cause;
    }
}
