package com.robustel.adapter.iot.thing;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.domain.elevator.ElevatorRegisteredEvent;
import com.robustel.dispatching.domain.robot.RobotRegisteredEvent;
import com.robustel.thing.application.registering_thing.RegisterThingCommand;
import com.robustel.thing.application.registering_thing.RegisteringDirectThingApplication;
import org.springframework.stereotype.Component;

/**
 * @author YangXuehong
 * @date 2022/4/13
 */
@Component
public class RegisteringThingService {
    private final RegisteringDirectThingApplication application;

    public RegisteringThingService(RegisteringDirectThingApplication application) {
        this.application = application;
    }

    @Subscribe
    public void registerElevator(ElevatorRegisteredEvent event) {
        String thingId = String.valueOf(event.getElevatorId().getValue());
        registerThing(thingId, event.getModelId());
    }

    @Subscribe
    public void registerRobot(RobotRegisteredEvent event) {
        String thingId = String.valueOf(event.getRobotId().getValue());
        registerThing(thingId, event.getModelId());
    }

    public void registerThing(String thingId, String modelId) {
        RegisterThingCommand command = new RegisterThingCommand(thingId, thingId,
                null, null, null, null, null, modelId, null);
        application.doRegisterDirectThing(command);
    }
}
