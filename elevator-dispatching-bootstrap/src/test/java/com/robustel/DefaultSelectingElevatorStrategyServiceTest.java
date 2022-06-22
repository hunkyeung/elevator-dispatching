package com.robustel;

import com.robustel.dispatching.domain.SelectingElevatorStrategyService;
import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.elevator.Passenger;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultSelectingElevatorStrategyServiceTest {

    @Test
    void Given_NotSuitableElevator_When_SelectorElevator_Then_ThrowsException() {
        ElevatorRepository repository = mock(ElevatorRepository.class);
        when(repository.findByCriteria(any())).thenReturn(List.of());
        DefaultSelectingElevatorStrategyService service = new DefaultSelectingElevatorStrategyService(repository);
        assertThrows(SelectingElevatorStrategyService.NoElevatorAvailableException.class,
                () -> service.selectElevator(Passenger.of("1"), Floor.of(1), Floor.of(5)));
    }

    @Test
    void Given_SuitableElevator_When_SelectorElevator_Then_Random() {
        ElevatorRepository repository = mock(ElevatorRepository.class);
        Elevator elevator1 = mock(Elevator.class);
        Elevator elevator2 = mock(Elevator.class);
        when(elevator1.isMatched(any(), any())).thenReturn(true);
        when(elevator2.isMatched(any(), any())).thenReturn(false);
        List<Elevator> elevatorList = List.of(elevator1, elevator2);
        when(repository.findByCriteria(any())).thenReturn(elevatorList);
        DefaultSelectingElevatorStrategyService service = new DefaultSelectingElevatorStrategyService(repository);
        Elevator elevator = service.selectElevator(Passenger.of("1"), Floor.of(1), Floor.of(5));
        assertTrue(elevatorList.contains(elevator));
        assertEquals(elevator1, elevator);
    }

}