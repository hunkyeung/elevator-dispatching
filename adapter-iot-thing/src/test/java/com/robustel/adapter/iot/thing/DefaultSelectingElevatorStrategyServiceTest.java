package com.robustel.adapter.iot.thing;

import com.robustel.ddd.query.Query;
import com.robustel.ddd.query.Type;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.elevator.Passenger;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DefaultSelectingElevatorStrategyServiceTest {

    @Test
    void test() {
        var repository = mock(ElevatorRepository.class);
        DefaultSelectingElevatorStrategyService service = new DefaultSelectingElevatorStrategyService(repository);
        var query = new Query.Builder()
                .matching(Type.IN, "binding", Arrays.asList(Passenger.of("1")))
                .build();
        service.select(Passenger.of("1"), Floor.of(1), Floor.of(2));
        verify(repository).findByCriteria(query);
    }

}