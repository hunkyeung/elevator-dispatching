package com.robustel.dispatching.application;

import com.robustel.ddd.service.EventPublisher;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.ddd.service.UidGenerator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UnregisteringElevatorApplicationTest {
    public static final MockedStatic<ServiceLocator> MOCKED_STATIC = mockStatic(ServiceLocator.class);

    @BeforeAll
    static void initAll() {
        MOCKED_STATIC.when(() -> ServiceLocator.service(UidGenerator.class)).thenReturn((UidGenerator) () -> 1);
        MOCKED_STATIC.when(() -> ServiceLocator.service(EventPublisher.class)).thenReturn(mock(EventPublisher.class));
    }

    @AfterAll
    static void close() {
        MOCKED_STATIC.close();

    }

    @Test
    void test() {
        ElevatorRepository repository = mock(ElevatorRepository.class);
        UnregisteringElevatorApplication application = new UnregisteringElevatorApplication(repository);
        application.doUnregister(0L);
        verify(repository).deleteById(0L);
    }

}