package com.robustel.dispatching.domain;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorId;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.robot.Robot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author YangXuehong
 * @date 2022/4/11
 */
@Component
@Slf4j
public class RandomDispatchingStrategyService implements DispatchingStrategyService {
    private final ElevatorRepository elevatorRepository;

    public RandomDispatchingStrategyService(ElevatorRepository elevatorRepository) {
        this.elevatorRepository = elevatorRepository;
    }

    @Override
    public Elevator selectElevator(Robot robot, Floor from, Floor to) {
//        List<Elevator> elevatorList = elevatorRepository.findAll().stream().filter(
//                elevator -> elevator.isValid(from, to)
//        ).collect(Collectors.toList());
//        if (elevatorList.isEmpty()) {
//            log.error("No elevator available. ");
//            throw new RuntimeException();
//        }
//        Elevator elevator = elevatorList.get(new Random().nextInt(elevatorList.size()));
        Elevator elevator = elevatorRepository.findById(ElevatorId.of("48bf009a-bc31-40e9-a6c0-f3a96f190fa3")).orElseThrow(
                () -> new RuntimeException()
        );
        return elevator;
    }
}
