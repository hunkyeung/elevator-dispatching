package com.robustel.adapter.iot.thing;

import com.robustel.ddd.query.Query;
import com.robustel.ddd.query.Type;
import com.robustel.dispatching.domain.AbstractSelectingElevatorStrategyService;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.elevator.Passenger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author YangXuehong
 * @date 2022/4/11
 */
@Component
@Slf4j
public class DefaultSelectingElevatorStrategyService extends AbstractSelectingElevatorStrategyService {
    private final ElevatorRepository elevatorRepository;

    public DefaultSelectingElevatorStrategyService(ElevatorRepository elevatorRepository) {
        this.elevatorRepository = elevatorRepository;
    }

    @Override
    protected Long select(Passenger passenger, Floor from, Floor to) {
        Query query = new Query.Builder()
                .matching(Type.IN, "binding", Arrays.asList(passenger))
                .build();
        return elevatorRepository.findByCriteria(query).stream().filter(
                        elevator -> elevator.isMatched(from, to)).map(elevator -> elevator.id()).findAny()
                .orElseThrow(() -> new NoElevatorAvailableException(passenger));
    }

}
