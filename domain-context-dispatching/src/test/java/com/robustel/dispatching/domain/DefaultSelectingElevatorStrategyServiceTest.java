package com.robustel.dispatching.domain;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.elevator.Passenger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultSelectingElevatorStrategyServiceTest {

    @BeforeAll
    static void initAll() {
        InitServiceLocator.init();
    }

    @Test
    void Given_NotSuitableElevator_When_SelectorElevator_Then_ThrowsException() {
        ElevatorRepository repository = mock(ElevatorRepository.class);
        when(repository.findByCriteria(any())).thenReturn(List.of());
        DefaultSelectingElevatorStrategyService service = new DefaultSelectingElevatorStrategyService(repository);
        assertThrows(SelectingElevatorStrategyService.NoElevatorAvailableException.class,
                () -> service.selectElevator(Passenger.of(1L), Floor.of(1), Floor.of(5)));
    }

    @Test
    void Given_SuitableElevator_When_SelectorElevator_Then_Random() {
        ElevatorRepository repository = mock(ElevatorRepository.class);
        List<Elevator> elevatorList = List.of(Elevator.create("foo", 10, -1, "modelId", "1234567890"),
                Elevator.create("bar", 100, -5, "modelId", "1234567890"));
        when(repository.findByCriteria(any())).thenReturn(elevatorList);
        DefaultSelectingElevatorStrategyService service = new DefaultSelectingElevatorStrategyService(repository);
        Elevator elevator = service.selectElevator(Passenger.of(1L), Floor.of(1), Floor.of(5));
        assertTrue(elevatorList.contains(elevator));
    }

}