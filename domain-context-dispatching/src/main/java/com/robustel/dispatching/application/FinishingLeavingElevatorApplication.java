package com.robustel.dispatching.application;

import com.google.common.eventbus.Subscribe;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.ddd.service.UidGenerator;
import com.robustel.dispatching.domain.elevator.*;
import com.robustel.dispatching.domain.requesthistory.RequestHistory;
import com.robustel.dispatching.domain.requesthistory.RequestHistoryId;
import com.robustel.dispatching.domain.requesthistory.RequestHistoryRepository;
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
public class FinishingLeavingElevatorApplication {
    private final RobotRepository robotRepository;
    private final ElevatorRepository elevatorRepository;
    private final RequestHistoryRepository requestHistoryRepository;

    public FinishingLeavingElevatorApplication(RobotRepository robotRepository, ElevatorRepository elevatorRepository, RequestHistoryRepository requestHistoryRepository) {
        this.robotRepository = robotRepository;
        this.elevatorRepository = elevatorRepository;
        this.requestHistoryRepository = requestHistoryRepository;
    }

    public void doFinishLeavingElevator(RobotId robotId, ElevatorId elevatorId) {
        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new ElevatorNotFoundException(elevatorId)
        );
        Robot robot = robotRepository.findById(robotId).orElseThrow(
                () -> new RobotNotFoundException(robotId)
        );
        elevator.leave(robot);
        robotRepository.save(robot);
        elevatorRepository.save(elevator);
    }

    @Subscribe
    public void listenOn(RobotLeftEvent event) {
        RequestHistory requestHistory = RequestHistory.of(
                RequestHistoryId.of(ServiceLocator.service(UidGenerator.class).nextId()), event.getRequest(), event.getElevatorId());
        requestHistoryRepository.save(requestHistory);
    }
}
