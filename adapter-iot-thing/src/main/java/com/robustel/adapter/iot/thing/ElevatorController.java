package com.robustel.adapter.iot.thing;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.domain.elevator.*;
import com.robustel.thing.application.ExecutingInstructionApplication;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author YangXuehong
 * @date 2022/4/12
 */
@Component
public class ElevatorController {
    private final ExecutingInstructionApplication executingInstructionApplication;

    public ElevatorController(ExecutingInstructionApplication executingInstructionApplication) {
        this.executingInstructionApplication = executingInstructionApplication;
    }

    public void take(ElevatorId elevatorId, Request request) {
        Map<String, Object> params = new HashMap<>();
        params.put("from", request.getFrom().getValue());
        params.put("to", request.getTo().getValue());
        executingInstructionApplication.doExecuteInstruction(
                String.valueOf(elevatorId.getValue()),
                "take", params);
    }

    public void release(ElevatorId elevatorId) {
        executingInstructionApplication.doExecuteInstruction(
                String.valueOf(elevatorId.getValue()),
                "release", new HashMap<>());
    }

    @Subscribe
    public void listenOn(RequestSummitedEvent event) {
        take(event.getElevatorId(), event.getRequest());
    }

    @Subscribe
    public void listenOn(ElevatorDoorReleasedEvent event) {
        release(event.getElevatorId());
    }

    @Subscribe
    public void listenOn(RobotLeftEvent event) {
        release(event.getElevatorId());
    }
}
