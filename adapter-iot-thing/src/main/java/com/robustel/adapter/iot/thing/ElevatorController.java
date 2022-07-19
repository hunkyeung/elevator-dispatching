package com.robustel.adapter.iot.thing;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.elevator.LightenEvent;
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

    public void lightUp(Long elevatorId, Floor floor) {
        Map<String, Object> params = new HashMap<>();
        params.put("floor", floor);
        executingInstructionApplication.doExecuteInstruction(
                String.valueOf(elevatorId),
                "take", params);
        log.debug("点亮电梯【{}】第【{}】层按钮", elevatorId, floor);
    }

    @Subscribe
    public void listenOn(LightenEvent event) {
        lightUp(event.getElevatorId(), event.getFloor());
    }

}
