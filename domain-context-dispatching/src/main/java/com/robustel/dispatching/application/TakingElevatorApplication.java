package com.robustel.dispatching.application;

import com.robustel.ddd.core.DomainException;
import com.robustel.dispatching.domain.SelectingElevatorStrategyService;
import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.elevator.Passenger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        Optional<Elevator> optionalElevator = selectingElevatorStrategyService.selectElevator(command.passenger, command.from, command.to);
        optionalElevator.ifPresent(
                elevator -> {
                    elevator.take(command.passenger, command.from, command.to);
                    elevatorRepository.save(elevator);
                }
        );
        return optionalElevator.orElseThrow(() -> new NoElevatorAvailableException(command.passenger)).id();
    }


    public record Command(Passenger passenger, Floor from, Floor to) {
    }


    public static class NoElevatorAvailableException extends DomainException {
        public NoElevatorAvailableException(Passenger passenger) {
            super(String.format("未找到合适的电梯给乘客【%s】，请确保该乘客已经绑定过电梯，且绑定的电梯处于可用状态", passenger.getId()));
        }
    }
}
