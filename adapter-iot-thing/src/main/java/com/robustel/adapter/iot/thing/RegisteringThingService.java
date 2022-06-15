package com.robustel.adapter.iot.thing;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.domain.elevator.ElevatorRegisteredEvent;
import com.robustel.dispatching.domain.robot.RobotRegisteredEvent;
import com.robustel.thing.application.registering_thing.RegisterThingCommand;
import com.robustel.thing.application.registering_thing.RegisteringDirectThingApplication;
import com.robustel.thing.application.registering_thing.RegisteringIndirectThingApplication;
import org.springframework.stereotype.Component;

/**
 * @author YangXuehong
 * @date 2022/4/13
 */
@Component
public class RegisteringThingService {
    private final RegisteringDirectThingApplication registeringDirectThingApplication;
    private final RegisteringIndirectThingApplication registeringIndirectThingApplication;

    public RegisteringThingService(RegisteringDirectThingApplication registeringDirectThingApplication, RegisteringIndirectThingApplication registeringIndirectThingApplication) {
        this.registeringDirectThingApplication = registeringDirectThingApplication;
        this.registeringIndirectThingApplication = registeringIndirectThingApplication;
    }

    @Subscribe
    public void registerElevator(ElevatorRegisteredEvent event) {
        String thingId = String.valueOf(event.getElevatorId());
        RegisterThingCommand command = new RegisterThingCommand(thingId, thingId,
                null, null, null
                , null, null
                , event.getModelId(), event.getSn());
        registeringIndirectThingApplication.doRegisterIndirectThing(command);
    }

    @Subscribe
    public void registerRobot(RobotRegisteredEvent event) {
        String thingId = String.valueOf(event.getRobotId());
        RegisterThingCommand command = new RegisterThingCommand(thingId, thingId,
                null, null, null
                , null, null
                , event.getModelId(), null);
        registeringDirectThingApplication.doRegisterDirectThing(command);
    }
}
