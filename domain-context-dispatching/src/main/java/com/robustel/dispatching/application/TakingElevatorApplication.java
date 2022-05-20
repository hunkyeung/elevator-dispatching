package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.SelectingElevatorStrategyService;
import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.robot.Robot;
import com.robustel.dispatching.domain.robot.RobotId;
import com.robustel.dispatching.domain.robot.RobotNotFoundException;
import com.robustel.dispatching.domain.robot.RobotRepository;
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
    private final RobotRepository robotRepository;
    private final ElevatorRepository elevatorRepository;

    public TakingElevatorApplication(SelectingElevatorStrategyService selectingElevatorStrategyService, RobotRepository robotRepository, ElevatorRepository elevatorRepository) {
        this.selectingElevatorStrategyService = selectingElevatorStrategyService;
        this.robotRepository = robotRepository;
        this.elevatorRepository = elevatorRepository;
    }

    public String doTakeElevator(RobotId robotId, Command command) {
        Robot robot = robotRepository.findById(robotId).orElseThrow(
                () -> new RobotNotFoundException(robotId)
        );
        Floor from = command.getFrom();
        Floor to = command.getTo();
        Elevator elevator = selectingElevatorStrategyService.selectElevator(robot, from, to);
        elevator.accept(robot.id(), from, to);
        elevatorRepository.save(elevator);
        return elevator.id().value();
    }

    @Getter
    public static class Command {
        private Floor from;
        private Floor to;
    }
}
