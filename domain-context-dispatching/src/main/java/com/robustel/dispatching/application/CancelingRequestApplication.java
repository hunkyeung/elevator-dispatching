package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorNotFoundException;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Passenger;
import com.robustel.dispatching.domain.requesthistory.RequestHistory;
import com.robustel.dispatching.domain.requesthistory.RequestHistoryRepository;
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
public class CancelingRequestApplication {
    private final ElevatorRepository elevatorRepository;
    private final RequestHistoryRepository requestHistoryRepository;

    public CancelingRequestApplication(ElevatorRepository elevatorRepository, RequestHistoryRepository requestHistoryRepository) {
        this.elevatorRepository = elevatorRepository;
        this.requestHistoryRepository = requestHistoryRepository;
    }

    public void doCancelRequest(Long elevatorId, Command command) {
        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new ElevatorNotFoundException(elevatorId)
        );
        RequestHistory requestHistory = elevator.cancelRequest(command.getPassenger(), command.getCause());
        requestHistoryRepository.save(requestHistory);
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
