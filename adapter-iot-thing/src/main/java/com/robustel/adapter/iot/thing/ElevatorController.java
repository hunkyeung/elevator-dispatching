package com.robustel.adapter.iot.thing;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.domain.elevator.ElevatorDoorReleasedEvent;
import com.robustel.dispatching.domain.elevator.ElevatorId;
import com.robustel.dispatching.domain.elevator.Request;
import com.robustel.dispatching.domain.elevator.RequestSummitedEvent;
import com.robustel.thing.application.ExecutingInstructionApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author YangXuehong
 * @date 2022/4/12
 */
@Component
@Slf4j
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
        log.info("机器人【{}】想搭乘电梯【{}】从{}楼到{}楼", request.getRobotId(), elevatorId, request.getFrom().getValue(), request.getTo().getValue());
    }

    public void release(ElevatorId elevatorId) {
        executingInstructionApplication.doExecuteInstruction(
                String.valueOf(elevatorId.getValue()),
                "release", new HashMap<>());
        log.info("释放电梯【{}】开门按钮", elevatorId);
    }

    @Subscribe
    public void listenOn(RequestSummitedEvent event) {
        take(event.getElevatorId(), event.getRequest());
    }

    @Subscribe
    public void listenOn(ElevatorDoorReleasedEvent event) {
        release(event.getElevatorId());
    }
}
