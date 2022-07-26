package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.InitServiceLocator;
import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RegisteringElevatorApplicationTest {

    private ElevatorRepository repository;
    private RegisteringElevatorApplication application;

    static {
        InitServiceLocator.init();
    }

    @BeforeEach
    void init() {
        repository = mock(ElevatorRepository.class);
        application = new RegisteringElevatorApplication(repository);
    }

    @Test
    void test() {
        RegisteringElevatorApplication.Command command = new RegisteringElevatorApplication.Command();
        command.setId(1);
        command.setName("foobar");
        command.setHighest(50);
        command.setLowest(-2);
        command.setModelId("2000");
        command.setSn("2020");
        application.doRegister(command);
        verify(repository).save(any(Elevator.class));
    }

}