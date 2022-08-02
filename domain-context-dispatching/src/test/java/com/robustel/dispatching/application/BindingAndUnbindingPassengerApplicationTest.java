package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Passenger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BindingAndUnbindingPassengerApplicationTest {

    private ElevatorRepository elevatorRepository;

    private BindingAndUnbindingPassengerApplication application;

    @BeforeEach
    void init() {
        elevatorRepository = mock(ElevatorRepository.class);
        application = new BindingAndUnbindingPassengerApplication(elevatorRepository);
    }

    @Test
    void Given_NotExistElevatorId_When_DoBind_Then_ThrowsElevatorNotFoundException() {
        when(elevatorRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(Elevator.ElevatorNotFoundException.class, () -> application.doBind(1L, "1"));
        verify(elevatorRepository).findById(1L);
        verify(elevatorRepository, never()).save(any(Elevator.class));
    }


    @Test
    void Given_ExistElevatorId_When_DoBind_Then_Expected() {
        Elevator elevator = mock(Elevator.class);
        when(elevatorRepository.findById(any())).thenReturn(Optional.ofNullable(elevator));
        application.doBind(1L, "1");
        verify(elevatorRepository).findById(1L);
        verify(elevator).bind(Passenger.of("1"));
        verify(elevatorRepository).save(any(Elevator.class));
    }

    @Test
    void Given_NotExistElevatorId_When_DoUnbind_Then_ThrowsElevatorNotFoundException() {
        when(elevatorRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(Elevator.ElevatorNotFoundException.class, () -> application.doUnbind(1L, "1"));
        verify(elevatorRepository).findById(1L);
        verify(elevatorRepository, never()).save(any(Elevator.class));
    }


    @Test
    void Given_ExistElevatorId_When_DoUnbind_Then_Expected() {
        Elevator elevator = mock(Elevator.class);
        when(elevatorRepository.findById(any())).thenReturn(Optional.ofNullable(elevator));
        application.doUnbind(1L, "1");
        verify(elevatorRepository).findById(1L);
        verify(elevator).unbind(Passenger.of("1"));
        verify(elevatorRepository).save(any(Elevator.class));
    }
}