package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.SelectingElevatorStrategyService;
import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.elevator.Passenger;
import lombok.Getter;
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
        Elevator elevator = selectingElevatorStrategyService.selectElevator(command.getPassenger(), command.getFrom(), command.getTo());
        elevator.take(command.getPassenger(), command.getFrom(), command.getTo());
        elevatorRepository.save(elevator);
        return elevator.id();
    }

    @Getter
    public static class Command {
        private Passenger passenger;
        private Floor from;
        private Floor to;
    }
}
