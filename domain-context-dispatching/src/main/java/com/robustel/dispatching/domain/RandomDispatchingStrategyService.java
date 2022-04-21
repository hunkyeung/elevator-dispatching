package com.robustel.dispatching.domain;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.robot.Robot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yeung.api.util.query.Query;
import org.yeung.api.util.query.Type;

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

    public RandomDispatchingStrategyService(ElevatorRepository elevatorRepository) {
        this.elevatorRepository = elevatorRepository;
    }

    @Override
    public Elevator selectElevator(Robot robot, Floor from, Floor to) {
        Query query = new Query.Builder().matching(Type.in, "whiteList", Arrays.asList(robot.getId())).build();
        List<Elevator> elevatorList = elevatorRepository.findByCriteria(query).stream().filter(
                elevator -> elevator.isValid(from, to)
        ).collect(Collectors.toList());
        if (elevatorList.isEmpty()) {
            log.error("No elevator available. ");
            throw new NoElevatorAvailableException(robot.getId());
        }
        Elevator elevator = elevatorList.get(new Random().nextInt(elevatorList.size()));
        return elevator;
    }
}
