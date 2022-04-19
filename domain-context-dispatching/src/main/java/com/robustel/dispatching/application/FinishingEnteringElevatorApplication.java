package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorId;
import com.robustel.dispatching.domain.elevator.ElevatorNotFoundException;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.robot.Robot;
import com.robustel.dispatching.domain.robot.RobotId;
import com.robustel.dispatching.domain.robot.RobotNotFoundException;
import com.robustel.dispatching.domain.robot.RobotRepository;
import org.springframework.stereotype.Service;

/**
 * @author YangXuehong
 * @date 2022/4/11
 */
@Service
public class FinishingEnteringElevatorApplication {
    private final RobotRepository robotRepository;
    private final ElevatorRepository elevatorRepository;

    public FinishingEnteringElevatorApplication(RobotRepository robotRepository, ElevatorRepository elevatorRepository) {
        this.robotRepository = robotRepository;
        this.elevatorRepository = elevatorRepository;
    }

    public void doFinishEnteringElevator(RobotId robotId, ElevatorId elevatorId) {
        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new ElevatorNotFoundException(elevatorId)
        );
        Robot robot = robotRepository.findById(robotId).orElseThrow(
                () -> new RobotNotFoundException(robotId)
        );
        robot.enter(elevator);
        robotRepository.save(robot);
        elevatorRepository.save(elevator);
    }
}
