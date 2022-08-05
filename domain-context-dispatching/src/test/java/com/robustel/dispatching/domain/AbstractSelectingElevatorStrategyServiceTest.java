package com.robustel.dispatching.domain;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.elevator.Passenger;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class AbstractSelectingElevatorStrategyServiceTest {

    private final Passenger passenger = Passenger.of("1");
    private final Floor from10 = Floor.of(10);
    private final Floor to10 = Floor.of(10);
    private final Floor from2 = Floor.of(2);

    @Test
    void Given_TheSameFloor_When_SelectElevator_Then_ThrowsInvalidFloorException() {
        AbstractSelectingElevatorStrategyService service = new AbstractSelectingElevatorStrategyService() {
            @Override
            protected Optional<Elevator> select(Passenger passenger, Floor from, Floor to) {
                return Optional.of(mock(Elevator.class));
            }
        };
        assertThrows(AbstractSelectingElevatorStrategyService.InvalidFloorException.class,
                () -> service.selectElevator(passenger, from10, to10));
    }

    @Test
    void Given_TheDifferentFloor_When_SelectElevator_Then_Expected() {
        AbstractSelectingElevatorStrategyService service = new AbstractSelectingElevatorStrategyService() {
            @Override
            protected Optional<Elevator> select(Passenger passenger, Floor from, Floor to) {
                return Optional.of(mock(Elevator.class));
            }
        };
        Optional<Elevator> elevator = service.selectElevator(passenger, from2, to10);
        assertFalse(elevator.isEmpty());
    }

    @Test
    void Given_Null_When_SelectElevator_Then_Exception() {
        AbstractSelectingElevatorStrategyService service = new AbstractSelectingElevatorStrategyService() {
            @Override
            protected Optional<Elevator> select(Passenger passenger, Floor from, Floor to) {
                return Optional.of(mock(Elevator.class));
            }
        };
        assertThrows(NullPointerException.class, () -> service.selectElevator(null, null, null));
        Passenger passenger = Passenger.of("1");
        Floor floor1 = Floor.of(1);
        assertThrows(NullPointerException.class, () -> service.selectElevator(passenger, null, null));
        assertThrows(NullPointerException.class, () -> service.selectElevator(passenger, floor1, null));
    }

}