package com.robustel;

import com.robustel.ddd.query.Query;
import com.robustel.ddd.query.Type;
import com.robustel.dispatching.domain.SelectingElevatorStrategyService;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.elevator.Passenger;
import com.robustel.thing.domain.thing_status.ThingStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author YangXuehong
 * @date 2022/4/11
 */
@Component
@Slf4j
public class DefaultSelectingElevatorStrategyService implements SelectingElevatorStrategyService {
    private final ThingStatusRepository thingStatusRepository;
    private final ElevatorRepository elevatorRepository;

    public DefaultSelectingElevatorStrategyService(ThingStatusRepository thingStatusRepository, ElevatorRepository elevatorRepository) {
        this.thingStatusRepository = thingStatusRepository;
        this.elevatorRepository = elevatorRepository;
    }

    @Override
    public Long selectElevator(Passenger passenger, Floor from, Floor to) {
        Query query = new Query.Builder()
                .matching(Type.IN, "binding", Arrays.asList(passenger))
                .build();
        List<String> elevatorList = elevatorRepository.findByCriteria(query).stream().filter(
                elevator -> elevator.isMatched(from, to)).map(elevator -> String.valueOf(elevator.id())).toList();
        List<String> thingStatusList = thingStatusRepository.findAll().stream().filter(
                thingStatus -> elevatorList.contains(thingStatus.getId())
        ).map(thingStatus -> thingStatus.getId()).toList();
        if (thingStatusList.isEmpty()) {
            throw new NoElevatorAvailableException(passenger);
        }
        return Long.valueOf(thingStatusList.get(0));
    }
}
