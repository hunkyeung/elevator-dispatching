package com.robustel.adapter.iot.thing;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.domain.elevator.PassengerInEvent;
import com.robustel.dispatching.domain.elevator.PassengerOutEvent;
import com.robustel.thing.application.ExecutingInstructionApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author YangXuehong
 * @date 2022/4/13
 */
@Component
@Slf4j
public class RobotController {
    private final ExecutingInstructionApplication executingInstructionApplication;

    public RobotController(ExecutingInstructionApplication executingInstructionApplication) {
        this.executingInstructionApplication = executingInstructionApplication;
    }

    public void in(Long robotId) {
        log.debug("请乘客【{}】进梯", robotId);
        executingInstructionApplication.doExecuteInstruction(String.valueOf(robotId), "enter", Map.of());
    }

    public void out(Long robotId) {
        log.debug("请乘客【{}】出梯", robotId);
        executingInstructionApplication.doExecuteInstruction(String.valueOf(robotId), "leave", Map.of());
    }

    @Subscribe
    public void listenOnOut(PassengerOutEvent event) {
        out(event.getPassenger().getId());
    }

    @Subscribe
    public void listenOnIn(PassengerInEvent event) {
        in(event.getPassenger().getId());
    }
}
