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
 * @date 2022/4/21
 */
@Service
public class BindingAndUnbindingElevatorApplication {
    private final ElevatorRepository elevatorRepository;
    private final RobotRepository robotRepository;

    public BindingAndUnbindingElevatorApplication(ElevatorRepository elevatorRepository, RobotRepository robotRepository) {
        this.elevatorRepository = elevatorRepository;
        this.robotRepository = robotRepository;
    }

    public void doBindElevator(RobotId robotId, ElevatorId elevatorId) {
        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new ElevatorNotFoundException(elevatorId)
        );
        Robot robot = robotRepository.findById(robotId).orElseThrow(
                () -> new RobotNotFoundException(robotId)
        );
        robot.bind(elevator);
        elevatorRepository.save(elevator);
        robotRepository.save(robot);
    }

    public void doUnbindElevator(RobotId robotId, ElevatorId elevatorId) {
        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new ElevatorNotFoundException(elevatorId)
        );
        Robot robot = robotRepository.findById(robotId).orElseThrow(
                () -> new RobotNotFoundException(robotId)
        );
        robot.unbind(elevator);
        elevatorRepository.save(elevator);
        robotRepository.save(robot);
    }
}
