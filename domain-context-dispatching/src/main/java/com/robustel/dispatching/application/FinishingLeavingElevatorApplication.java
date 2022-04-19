package com.robustel.dispatching.application;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.domain.elevator.*;
import com.robustel.dispatching.domain.requesthistory.RequestHistory;
import com.robustel.dispatching.domain.requesthistory.RequestHistoryId;
import com.robustel.dispatching.domain.requesthistory.RequestHistoryRepository;
import com.robustel.dispatching.domain.robot.Robot;
import com.robustel.dispatching.domain.robot.RobotId;
import com.robustel.dispatching.domain.robot.RobotNotFoundException;
import com.robustel.dispatching.domain.robot.RobotRepository;
import org.springframework.stereotype.Service;
import org.yeung.api.util.UidGenerator;

/**
 * @author YangXuehong
 * @date 2022/4/11
 */
@Service
public class FinishingLeavingElevatorApplication {
    private final RobotRepository robotRepository;
    private final ElevatorRepository elevatorRepository;
    private final RequestHistoryRepository requestHistoryRepository;
    private final UidGenerator uidGenerator;

    public FinishingLeavingElevatorApplication(RobotRepository robotRepository, ElevatorRepository elevatorRepository, RequestHistoryRepository requestHistoryRepository, UidGenerator uidGenerator) {
        this.robotRepository = robotRepository;
        this.elevatorRepository = elevatorRepository;
        this.requestHistoryRepository = requestHistoryRepository;
        this.uidGenerator = uidGenerator;
    }

    public void doFinishLeavingElevator(RobotId robotId, ElevatorId elevatorId) {
        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(
                () -> new ElevatorNotFoundException(elevatorId)
        );
        Robot robot = robotRepository.findById(robotId).orElseThrow(
                () -> new RobotNotFoundException(robotId)
        );
        robot.leave(elevator);
        robotRepository.save(robot);
        elevatorRepository.save(elevator);
    }

    @Subscribe
    public void listenOn(RobotLeftEvent event) {
        RequestHistory requestHistory = RequestHistory.of(
                RequestHistoryId.of(uidGenerator.nextId()), event.getRequest(), event.getElevatorId());
        requestHistoryRepository.save(requestHistory);
    }
}
