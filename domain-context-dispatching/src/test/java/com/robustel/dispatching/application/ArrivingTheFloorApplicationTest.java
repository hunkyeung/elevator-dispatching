package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.elevator.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ArrivingTheFloorApplicationTest {

    private ElevatorRepository repository;
    private ArrivingTheFloorApplication application;

    @BeforeEach
    void init() {
        repository = mock(ElevatorRepository.class);
        application = new ArrivingTheFloorApplication(repository);
    }

    @Test
    void Given_NotExistElevatorId_When_DoArrive_Then_ThrowsElevatorNotFoundException() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        Assertions.assertThrows(ElevatorNotFoundException.class,
                () -> application.doArrive(0L, Floor.of(1), Direction.STOP));
    }

    @Test
    void Given_ExistElevatorId_When_DoArrive_Then_Expected() {
        Elevator elevator = mock(Elevator.class);
        when(repository.findById(any())).thenReturn(Optional.ofNullable(elevator));
        application.doArrive(0L, Floor.of(1), Direction.STOP);
        verify(elevator).arrive(Floor.of(1), Direction.STOP);
        verify(repository).findById(0L);
        verify(repository).save(elevator);
    }
}