package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ReleasingElevatorApplicationTest {

    private ElevatorRepository repository;
    private ReleasingElevatorApplication application;

    @BeforeEach
    void init() {
        repository = mock(ElevatorRepository.class);
        application = new ReleasingElevatorApplication(repository);
    }

    @Test
    void Given_NotExistElevator_When_DoRelease_Then_ElevatorNotFoundException() {
        assertThrows(Elevator.ElevatorNotFoundException.class, () -> application.doReleaseElevator(1L));
    }

    @Test
    void Given_ExistElevator_When_DoRelease_Then_Expected() {
        Elevator elevator = mock(Elevator.class);
        when(repository.findById(any())).thenReturn(Optional.ofNullable(elevator));
        application.doReleaseElevator(1L);
        verify(repository).findById(any());
        verify(repository).save(elevator);
    }

}