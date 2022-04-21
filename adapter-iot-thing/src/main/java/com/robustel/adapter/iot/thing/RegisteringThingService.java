package com.robustel.adapter.iot.thing;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.domain.elevator.ElevatorRegisteredEvent;
import com.robustel.dispatching.domain.robot.RobotRegisteredEvent;
import com.robustel.thing.application.registering_thing.RegisterThingCommand;
import com.robustel.thing.application.registering_thing.RegisteringDirectThingApplication;
import com.robustel.thing.application.registering_thing.RegisteringIndirectThingApplication;
import com.robustel.thing.domain.thing.CloudProperty;
import com.robustel.thing.domain.thing.Tag;
import com.robustel.thing.domain.thing_model.ChannelType;
import com.robustel.thing.domain.thing_model.TriggerAndEdgeKey;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

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
        String thingId = String.valueOf(event.getElevatorId().getValue());
        registerIndirectThing(thingId, event.getParams());
    }

    @Subscribe
    public void registerRobot(RobotRegisteredEvent event) {
        String thingId = String.valueOf(event.getRobotId().getValue());
        registerDirectThing(thingId, event.getParams());
    }

    public void registerIndirectThing(String thingId, Map<String, Object> params) {
        RegisterThingCommand command = new RegisterThingCommand(thingId, thingId,
                (String) params.get("area"), (ChannelType) params.get("channelType"), (Set<Tag>) params.get("tags")
                , (Set<CloudProperty>) params.get("properties"), (Set<TriggerAndEdgeKey>) params.get("triggerAndEdgeKeys")
                , (String) params.get("modelId"), (String) params.get("sn"));
        registeringIndirectThingApplication.doRegisterIndirectThing(command);
    }

    public void registerDirectThing(String thingId, Map<String, Object> params) {
        RegisterThingCommand command = new RegisterThingCommand(thingId, thingId,
                (String) params.get("area"), (ChannelType) params.get("channelType"), (Set<Tag>) params.get("tags")
                , (Set<CloudProperty>) params.get("properties"), (Set<TriggerAndEdgeKey>) params.get("triggerAndEdgeKeys")
                , (String) params.get("modelId"), (String) params.get("sn"));
        registeringDirectThingApplication.doRegisterDirectThing(command);
    }
}
