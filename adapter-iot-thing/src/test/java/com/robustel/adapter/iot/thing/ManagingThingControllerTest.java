package com.robustel.adapter.iot.thing;

import com.robustel.ddd.service.ServiceLocator;
import com.robustel.ddd.service.UidGenerator;
import com.robustel.dispatching.domain.elevator.ElevatorRegisteredEvent;
import com.robustel.dispatching.domain.elevator.ElevatorUnregisteredEvent;
import com.robustel.dispatching.domain.robot.RobotRegisteredEvent;
import com.robustel.thing.application.RemovingThingApplication;
import com.robustel.thing.application.registering_thing.RegisterThingCommand;
import com.robustel.thing.application.registering_thing.RegisteringDirectThingApplication;
import com.robustel.thing.application.registering_thing.RegisteringIndirectThingApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ManagingThingControllerTest {

    public static final MockedStatic<ServiceLocator> MOCKED_STATIC = mockStatic(ServiceLocator.class);
    private RegisteringDirectThingApplication registeringDirectThingApplication;
    private RegisteringIndirectThingApplication registeringIndirectThingApplication;
    private RemovingThingApplication removingThingApplication;
    private ManagingThingController controller;

    @BeforeAll
    static void initAll() {
        MOCKED_STATIC.when(() -> ServiceLocator.service(UidGenerator.class)).thenReturn((UidGenerator) () -> 1);
    }

    @AfterAll
    static void destroy() {
        MOCKED_STATIC.close();
    }

    @BeforeEach
    void init() {
        this.registeringDirectThingApplication = mock(RegisteringDirectThingApplication.class);
        registeringIndirectThingApplication = mock(RegisteringIndirectThingApplication.class);
        removingThingApplication = mock(RemovingThingApplication.class);
        this.controller = new ManagingThingController(registeringDirectThingApplication, registeringIndirectThingApplication, removingThingApplication);

    }

    @Test
    void testRegisterThing() {
        var controller = this.controller;
        var command = new RegisterThingCommand("1", "1",
                null, null, null
                , null, null
                , "foo", "bar");
        controller.registerThing(new ElevatorRegisteredEvent(1L, "foo", "bar"));
        verify(registeringIndirectThingApplication).doRegisterIndirectThing(any());
    }

    @Test
    void testRemovingThing() {
        controller.removeThing(new ElevatorUnregisteredEvent(1L));
        verify(removingThingApplication).doRemoveThing("1");
    }

    @Test
    void testRegisterRobot() {
        controller.registerRobot(new RobotRegisteredEvent(1L, "foobar"));
        verify(registeringDirectThingApplication).doRegisterDirectThing(any());
    }

}