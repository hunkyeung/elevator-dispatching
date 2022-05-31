package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorNotFoundException;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Passenger;
import com.robustel.dispatching.domain.robot.Robot;
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

    public void doBindToElevator(Long elevatorId, Long robotId) {
        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new ElevatorNotFoundException(elevatorId)
        );
        Robot robot = robotRepository.findById(robotId).orElseThrow(
                () -> new RobotNotFoundException(robotId)
        );
        elevator.bind(Passenger.of(robot.id()));
        elevatorRepository.save(elevator);
    }

    public void doUnbindFromElevator(Long elevatorId, Long robotId) {
        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new ElevatorNotFoundException(elevatorId)
        );
        Robot robot = robotRepository.findById(robotId).orElseThrow(
                () -> new RobotNotFoundException(robotId)
        );
        elevator.unbind(Passenger.of(robot.id()));
        elevatorRepository.save(elevator);
    }
}
