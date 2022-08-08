package com.robustel.dispatching.application;

import com.robustel.ddd.service.EventPublisher;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.ddd.service.UidGenerator;
import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegisteringElevatorApplicationTest {

    private ElevatorRepository repository;
    private RegisteringElevatorApplication application;

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

    @BeforeEach
    void init() {
        repository = mock(ElevatorRepository.class);
        application = new RegisteringElevatorApplication(repository);
    }

    @Test
    void test() {
        RegisteringElevatorApplication.Command command = new RegisteringElevatorApplication.Command(1, "foobar", 50, -2, "2000", "2020");
        application.doRegister(command);
        verify(repository).save(any(Elevator.class));
    }

}