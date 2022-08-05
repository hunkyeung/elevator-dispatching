package com.robustel.adapter.iot.thing;

import com.robustel.dispatching.domain.elevator.ElevatorRegisteredEvent;
import com.robustel.dispatching.domain.elevator.ElevatorUnregisteredEvent;
import com.robustel.dispatching.domain.robot.RobotRegisteredEvent;
import com.robustel.thing.application.RemovingThingApplication;
import com.robustel.thing.application.registering_thing.RegisterThingCommand;
import com.robustel.thing.application.registering_thing.RegisteringDirectThingApplication;
import com.robustel.thing.application.registering_thing.RegisteringIndirectThingApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ManagingThingControllerTest {

    private RegisteringDirectThingApplication registeringDirectThingApplication;
    private RegisteringIndirectThingApplication registeringIndirectThingApplication;
    private RemovingThingApplication removingThingApplication;
    private ManagingThingController controller;

    @BeforeEach
    void init() {
        InitServiceLocator.init();
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