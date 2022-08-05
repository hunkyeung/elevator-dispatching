package com.robustel.adapter.iot.thing;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.domain.elevator.ElevatorRegisteredEvent;
import com.robustel.dispatching.domain.elevator.ElevatorUnregisteredEvent;
import com.robustel.dispatching.domain.robot.RobotRegisteredEvent;
import com.robustel.thing.application.RemovingThingApplication;
import com.robustel.thing.application.registering_thing.RegisterThingCommand;
import com.robustel.thing.application.registering_thing.RegisteringDirectThingApplication;
import com.robustel.thing.application.registering_thing.RegisteringIndirectThingApplication;
import org.springframework.stereotype.Component;

/**
 * @author YangXuehong
 * @date 2022/4/13
 */
@Component
public class ManagingThingController {
    private final RegisteringDirectThingApplication registeringDirectThingApplication;
    private final RegisteringIndirectThingApplication registeringIndirectThingApplication;
    private final RemovingThingApplication removingThingApplication;

    public ManagingThingController(RegisteringDirectThingApplication registeringDirectThingApplication, RegisteringIndirectThingApplication registeringIndirectThingApplication, RemovingThingApplication removingThingApplication) {
        this.registeringDirectThingApplication = registeringDirectThingApplication;
        this.registeringIndirectThingApplication = registeringIndirectThingApplication;
        this.removingThingApplication = removingThingApplication;
    }

    @Subscribe
    public void registerThing(ElevatorRegisteredEvent event) {
        var thingId = String.valueOf(event.getElevatorId());
        var command = new RegisterThingCommand(thingId, thingId,
                null, null, null
                , null, null
                , event.getModelId(), event.getSn());
        registeringIndirectThingApplication.doRegisterIndirectThing(command);
    }

    @Subscribe
    public void removeThing(ElevatorUnregisteredEvent event) {
        removingThingApplication.doRemoveThing(String.valueOf(event.getElevatorId()));
    }

    @Subscribe
    public void registerRobot(RobotRegisteredEvent event) {
        var thingId = String.valueOf(event.getRobotId());
        var command = new RegisterThingCommand(thingId, thingId,
                null, null, null
                , null, null
                , event.getModelId(), null);
        registeringDirectThingApplication.doRegisterDirectThing(command);
    }
}
