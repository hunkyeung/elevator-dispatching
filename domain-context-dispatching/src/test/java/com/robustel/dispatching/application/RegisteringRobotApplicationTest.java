package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.InitServiceLocator;
import com.robustel.dispatching.domain.robot.Robot;
import com.robustel.dispatching.domain.robot.RobotRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RegisteringRobotApplicationTest {

    @Test
    void test() {
        InitServiceLocator.init();
        RobotRepository robotRepository = mock(RobotRepository.class);
        RegisteringRobotApplication registeringRobotApplication = new RegisteringRobotApplication(robotRepository);
        RegisteringRobotApplication.Command command = new RegisteringRobotApplication.Command("foobar", "2000");
        long id = registeringRobotApplication.doRegister(command);
        assertNotEquals(0, id);
        verify(robotRepository).save(any(Robot.class));
    }

}