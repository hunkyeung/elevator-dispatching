package com.robustel.adapter.iot.thing;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.domain.elevator.ReleaseDoorEvent;
import com.robustel.dispatching.domain.elevator.Request;
import com.robustel.dispatching.domain.elevator.RequestAcceptedEvent;
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

    public void take(Long elevatorId, Request request) {
        Map<String, Object> params = new HashMap<>();
        params.put("from", request.getFrom().getValue());
        params.put("to", request.getTo().getValue());
        executingInstructionApplication.doExecuteInstruction(
                String.valueOf(elevatorId),
                "take", params);
        log.debug("乘客【{}】想搭乘电梯【{}】从{}楼到{}楼", request.getPassenger(), elevatorId, request.getFrom().getValue(),
                request.getTo().getValue());
    }

    public void release(Long elevatorId) {
        executingInstructionApplication.doExecuteInstruction(
                String.valueOf(elevatorId),
                "release", Map.of());
        log.debug("释放电梯【{}】开门按钮", elevatorId);
    }

    @Subscribe
    public void listenOn(RequestAcceptedEvent event) {
        take(event.getElevatorId(), event.getRequest());
    }

    @Subscribe
    public void listenOn(ReleaseDoorEvent event) {
        release(event.getElevatorId());
    }


}
