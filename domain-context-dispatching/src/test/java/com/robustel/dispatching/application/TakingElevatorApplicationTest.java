package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.SelectingElevatorStrategyService;
import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.elevator.Passenger;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TakingElevatorApplicationTest {

    public static final SelectingElevatorStrategyService SELECTING_ELEVATOR_STRATEGY_SERVICE = mock(SelectingElevatorStrategyService.class);
    public static final ElevatorRepository ELEVATOR_REPOSITORY = mock(ElevatorRepository.class);

    @Test
    void test() {
        Elevator elevator = mock(Elevator.class);
        when(SELECTING_ELEVATOR_STRATEGY_SERVICE.selectElevator(any(), any(), any())).thenReturn(Optional.of(elevator));
        TakingElevatorApplication.Command command = new TakingElevatorApplication.Command(Passenger.of("1"), Floor.of(1), Floor.of(10));
        TakingElevatorApplication application = new TakingElevatorApplication(SELECTING_ELEVATOR_STRATEGY_SERVICE, ELEVATOR_REPOSITORY);
        application.doTakeElevator(command);
        verify(SELECTING_ELEVATOR_STRATEGY_SERVICE).selectElevator(Passenger.of("1"), Floor.of(1), Floor.of(10));
        verify(elevator).take(Passenger.of("1"), Floor.of(1), Floor.of(10));
        verify(ELEVATOR_REPOSITORY).save(elevator);
    }

    @Test
    void testTakeButNoElevatorAvailableException() {
        when(SELECTING_ELEVATOR_STRATEGY_SERVICE.selectElevator(any(), any(), any())).thenReturn(Optional.empty());
        TakingElevatorApplication.Command command = new TakingElevatorApplication.Command(Passenger.of("1"), Floor.of(1), Floor.of(10));
        TakingElevatorApplication application = new TakingElevatorApplication(SELECTING_ELEVATOR_STRATEGY_SERVICE, ELEVATOR_REPOSITORY);
        assertThrows(TakingElevatorApplication.NoElevatorAvailableException.class, () -> application.doTakeElevator(command));
    }

}