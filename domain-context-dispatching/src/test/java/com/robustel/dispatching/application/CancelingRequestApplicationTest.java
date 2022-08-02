package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.elevator.Elevator;
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

class CancelingRequestApplicationTest {
    private ElevatorRepository elevatorRepository;
    private RequestHistoryRepository requestHistoryRepository;

    private CancelingRequestApplication application;

    @BeforeEach
    void init() {
        elevatorRepository = mock(ElevatorRepository.class);
        requestHistoryRepository = mock(RequestHistoryRepository.class);
        application = new CancelingRequestApplication(elevatorRepository, requestHistoryRepository);
    }

    @Test
    void Given_NotExistElevatorId_When_DoCancelRequest_Then_ThrowsElevatorNotFoundException() {
        when(elevatorRepository.findById(any())).thenReturn(Optional.empty());
        CancelingRequestApplication.Command command = new CancelingRequestApplication.Command(Passenger.of("1"), "for test");
        assertThrows(Elevator.ElevatorNotFoundException.class, () -> application.doCancelRequest(1L, command));
        verify(elevatorRepository).findById(1L);
        verify(elevatorRepository, never()).save(any(Elevator.class));
    }


    @Test
    void Given_ExistElevatorId_When_DoCancelRequest_Then_Expected() {
        Elevator elevator = mock(Elevator.class);
        when(elevator.cancelRequest(Passenger.of("1"), "for test")).thenReturn(mock(RequestHistory.class));
        when(elevatorRepository.findById(any())).thenReturn(Optional.of(elevator));
        CancelingRequestApplication.Command command = new CancelingRequestApplication.Command(Passenger.of("1"), "for test");
        application.doCancelRequest(1L, command);
        verify(elevatorRepository).findById(1L);
        verify(elevator).cancelRequest(Passenger.of("1"), "for test");
        verify(elevatorRepository).save(any(Elevator.class));
        verify(requestHistoryRepository).save(any(RequestHistory.class));
    }

}