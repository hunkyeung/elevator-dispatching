package com.robustel.dispatching.domain;

import com.robustel.ddd.query.Query;
import com.robustel.ddd.query.Type;
import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.robot.Robot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author YangXuehong
 * @date 2022/4/11
 */
@Component
@Slf4j
public class RandomDispatchingStrategyService implements DispatchingStrategyService {
    private final ElevatorRepository elevatorRepository;
    private static final Random random = new Random();

    public RandomDispatchingStrategyService(ElevatorRepository elevatorRepository) {
        this.elevatorRepository = elevatorRepository;
    }

    @Override
    public Elevator selectElevator(Robot robot, Floor from, Floor to) {
        Query query = new Query.Builder().matching(Type.IN, "whiteList", Arrays.asList(robot.id())).build();
        List<Elevator> elevatorList = elevatorRepository.findByCriteria(query).stream().filter(
                elevator -> elevator.isValid(from, to)
        ).collect(Collectors.toList());
        if (elevatorList.isEmpty()) {
            throw new NoElevatorAvailableException(robot.id());
        }
        return elevatorList.get(random.nextInt(elevatorList.size()));
    }
}
