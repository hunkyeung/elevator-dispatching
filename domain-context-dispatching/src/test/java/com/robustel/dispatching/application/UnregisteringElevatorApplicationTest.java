package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.InitServiceLocator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class UnregisteringElevatorApplicationTest {
    @BeforeAll
    public static void initAll() {
        InitServiceLocator.init();
    }

    @Test
    void test() {
        ElevatorRepository repository = mock(ElevatorRepository.class);
        UnregisteringElevatorApplication application = new UnregisteringElevatorApplication(repository);
        application.doUnregister(0L);
        verify(repository).deleteById(0L);
    }

}