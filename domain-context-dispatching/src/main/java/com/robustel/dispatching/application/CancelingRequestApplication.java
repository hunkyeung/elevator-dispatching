package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorId;
import com.robustel.dispatching.domain.elevator.ElevatorNotFoundException;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.robot.RobotId;
import org.springframework.stereotype.Service;

/**
 * @author YangXuehong
 * @date 2022/4/19
 */
@Service
public class CancelingRequestApplication {
    private final ElevatorRepository repository;

    public CancelingRequestApplication(ElevatorRepository repository) {
        this.repository = repository;
    }

    public void doCancelRequest(RobotId robotId, ElevatorId elevatorId) {
        Elevator elevator = repository.findById(elevatorId).orElseThrow(
                () -> new ElevatorNotFoundException(elevatorId)
        );
        elevator.cancelRequest(robotId);
        repository.save(elevator);
    }
}
