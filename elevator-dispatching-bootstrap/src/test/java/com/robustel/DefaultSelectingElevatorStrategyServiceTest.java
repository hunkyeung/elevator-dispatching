package com.robustel;

import com.robustel.dispatching.domain.SelectingElevatorStrategyService;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.elevator.Passenger;
import com.robustel.thing.domain.thing_status.ThingStatus;
import com.robustel.thing.domain.thing_status.ThingStatusRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultSelectingElevatorStrategyServiceTest {

    @Test
    void Given_NotSuitableElevator_When_SelectorElevator_Then_ThrowsException() {
        ThingStatusRepository repository = mock(ThingStatusRepository.class);
        when(repository.findAll()).thenReturn(List.of());
        ElevatorRepository elevatorRepository = mock(ElevatorRepository.class);
        DefaultSelectingElevatorStrategyService service = new DefaultSelectingElevatorStrategyService(repository, elevatorRepository);
        assertThrows(SelectingElevatorStrategyService.NoElevatorAvailableException.class,
                () -> service.selectElevator(Passenger.of("1"), Floor.of(1), Floor.of(5)));
    }

    @Test
    void Given_SuitableElevator_When_SelectorElevator_Then_Random() {
        ThingStatusRepository repository = mock(ThingStatusRepository.class);
        ThingStatus elevator1 = mock(ThingStatus.class);
        ThingStatus elevator2 = mock(ThingStatus.class);

        List<ThingStatus> elevatorList = List.of(elevator1, elevator2);
        when(repository.findAll()).thenReturn(elevatorList);
        ElevatorRepository elevatorRepository = mock(ElevatorRepository.class);
        DefaultSelectingElevatorStrategyService service = new DefaultSelectingElevatorStrategyService(repository, elevatorRepository);
        //todo
    }

}