package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorNotFoundException;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Passenger;
import com.robustel.dispatching.domain.requesthistory.RequestHistory;
import com.robustel.dispatching.domain.requesthistory.RequestHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FinishingApplicationTest {
    private ElevatorRepository elevatorRepository;
    private RequestHistoryRepository requestHistoryRepository;

    private FinishingApplication application;

    @BeforeEach
    void init() {
        elevatorRepository = mock(ElevatorRepository.class);
        requestHistoryRepository = mock(RequestHistoryRepository.class);
        application = new FinishingApplication(elevatorRepository, requestHistoryRepository);
    }

    @Test
    void Given_NotExistElevatorId_When_DoCancelRequest_Then_ThrowsElevatorNotFoundException() {
        when(elevatorRepository.findById(any())).thenReturn(Optional.empty());
        FinishingApplication.Command command = new FinishingApplication.Command(Passenger.of("1"));
        assertThrows(ElevatorNotFoundException.class, () -> application.doFinish(1L, command));
        verify(elevatorRepository).findById(1L);
        verify(elevatorRepository, never()).save(any(Elevator.class));
    }


    @Test
    void Given_ExistElevatorIdAndOnPassage_When_DoFinish_Then_Expected() {
        Elevator elevator = mock(Elevator.class);
        when(elevator.finish(Passenger.of("1"))).thenReturn(Optional.ofNullable(mock(RequestHistory.class)));
        when(elevatorRepository.findById(any())).thenReturn(Optional.ofNullable(elevator));
        FinishingApplication.Command command = new FinishingApplication.Command(Passenger.of("1"));
        application.doFinish(1L, command);
        verify(elevatorRepository).findById(1L);
        verify(elevator).finish(Passenger.of("1"));
        verify(elevatorRepository).save(any(Elevator.class));
        verify(requestHistoryRepository).save(any(RequestHistory.class));
    }

    @Test
    void Given_ExistElevatorIdAndNotOnPassage_When_DoFinish_Then_Expected() {
        Elevator elevator = mock(Elevator.class);
        when(elevator.finish(Passenger.of("1"))).thenReturn(Optional.empty());
        when(elevatorRepository.findById(any())).thenReturn(Optional.ofNullable(elevator));
        FinishingApplication.Command command = new FinishingApplication.Command(Passenger.of("1"));
        application.doFinish(1L, command);
        verify(elevatorRepository).findById(1L);
        verify(elevator).finish(Passenger.of("1"));
        verify(elevatorRepository).save(any(Elevator.class));
        verify(requestHistoryRepository, never()).save(any(RequestHistory.class));
    }
}