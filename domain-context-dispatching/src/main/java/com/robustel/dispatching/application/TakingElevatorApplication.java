package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.DispatchingStrategyService;
import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.elevator.Request;
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
    private final DispatchingStrategyService dispatchingService;
    private final RobotRepository robotRepository;
    private final ElevatorRepository elevatorRepository;

    public TakingElevatorApplication(DispatchingStrategyService dispatchingService, RobotRepository robotRepository, ElevatorRepository elevatorRepository) {
        this.dispatchingService = dispatchingService;
        this.robotRepository = robotRepository;
        this.elevatorRepository = elevatorRepository;
    }

    public String doTakeElevator(RobotId robotId, Command command) {
        Robot robot = robotRepository.findById(robotId).orElseThrow(
                () -> new RobotNotFoundException(robotId)
        );
        Floor from = command.getFrom();
        Floor to = command.getTo();
        Elevator elevator = dispatchingService.selectElevator(robot, from, to);
        Request request = Request.of(robot.id(), from, to);
        elevator.accept(request);
        elevatorRepository.save(elevator);
        return elevator.id().value();
    }

    @Getter
    public static class Command {
        private Floor from;
        private Floor to;
    }
}
