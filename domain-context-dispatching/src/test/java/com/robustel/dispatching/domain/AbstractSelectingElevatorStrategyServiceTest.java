package com.robustel.dispatching.domain;

import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.elevator.Passenger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AbstractSelectingElevatorStrategyServiceTest {

    @Test
    void Given_TheSameFloor_When_SelectElevator_Then_ThrowsIllegalArgumentException() {
        AbstractSelectingElevatorStrategyService service = new AbstractSelectingElevatorStrategyService() {
            @Override
            protected Long select(Passenger passenger, Floor from, Floor to) {
                return 100L;
            }
        };
        assertThrows(IllegalArgumentException.class,
                () -> service.selectElevator(Passenger.of("1"), Floor.of(10), Floor.of(10)));
    }

    @Test
    void Given_TheDifferentFloor_When_SelectElevator_Then_Expected() {
        AbstractSelectingElevatorStrategyService service = new AbstractSelectingElevatorStrategyService() {
            @Override
            protected Long select(Passenger passenger, Floor from, Floor to) {
                return 100L;
            }
        };
        Long elevatorId = service.selectElevator(Passenger.of("1"), Floor.of(2), Floor.of(10));
        assertEquals(100, elevatorId);
    }

}