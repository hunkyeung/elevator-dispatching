package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.SelectingElevatorStrategyService;
import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.elevator.Passenger;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author YangXuehong
 * @date 2022/4/8
 */
@Service
@Slf4j
public class TakingElevatorApplication {
    private final SelectingElevatorStrategyService selectingElevatorStrategyService;
    private final ElevatorRepository elevatorRepository;

    public TakingElevatorApplication(SelectingElevatorStrategyService selectingElevatorStrategyService, ElevatorRepository elevatorRepository) {
        this.selectingElevatorStrategyService = selectingElevatorStrategyService;
        this.elevatorRepository = elevatorRepository;
    }

    public Long doTakeElevator(Command command) {
        log.debug("等待调度{}", command);
        Long elevatorId = selectingElevatorStrategyService.selectElevator(command.passenger, command.from, command.to);
        Elevator elevator = elevatorRepository.findById(elevatorId).get();
        elevator.take(command.passenger, command.from, command.to);
        elevatorRepository.save(elevator);
        return elevator.id();
    }

    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Command {
        private Passenger passenger;
        private Floor from;
        private Floor to;
    }
}
