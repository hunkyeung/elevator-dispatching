package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.SelectingElevatorStrategyService;
import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.elevator.Passenger;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
        if (Objects.equals(command.getFrom(), command.getTo())) {
            throw new IllegalArgumentException(String.format("出发楼层【%s】和目标楼层【%s】相同", command.getFrom(), command.getTo()));
        }
        Elevator elevator = selectingElevatorStrategyService.selectElevator(command.getPassenger(), command.getFrom(), command.getTo());
        elevator.take(command.getPassenger(), command.getFrom(), command.getTo());
        elevatorRepository.save(elevator);
        return elevator.id();
    }

    @ToString
    @Getter
    public static class Command {
        @NonNull
        private Passenger passenger;
        @NonNull
        private Floor from;
        @NonNull
        private Floor to;
    }
}
