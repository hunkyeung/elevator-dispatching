package com.robustel.adapter.iot.thing;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.domain.elevator.AllPassengerInRespondedEvent;
import com.robustel.dispatching.domain.elevator.NoPassengerEvent;
import com.robustel.dispatching.domain.elevator.TakingRequest;
import com.robustel.dispatching.domain.elevator.TakingRequestAcceptedEvent;
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

    public void take(Long elevatorId, TakingRequest takingRequest) {
        Map<String, Object> params = new HashMap<>();
        params.put("from", takingRequest.getFrom().getValue());
        params.put("to", takingRequest.getTo().getValue());
        executingInstructionApplication.doExecuteInstruction(
                String.valueOf(elevatorId),
                "take", params);
        log.debug("乘客【{}】想搭乘电梯【{}】从{}楼到{}楼", takingRequest.getPassenger(), elevatorId, takingRequest.getFrom().getValue(),
                takingRequest.getTo().getValue());
    }

    public void release(Long elevatorId) {
        executingInstructionApplication.doExecuteInstruction(
                String.valueOf(elevatorId),
                "release", Map.of());
        log.debug("释放电梯【{}】开门按钮", elevatorId);
    }

    @Subscribe
    public void listenOn(TakingRequestAcceptedEvent event) {
        take(event.getElevatorId(), event.getTakingRequest());
    }

    @Subscribe
    public void listenOn(NoPassengerEvent event) {
        release(event.getElevatorId());
    }

    @Subscribe
    public void listenOn(AllPassengerInRespondedEvent event) {
        release(event.getElevatorId());
    }

}
