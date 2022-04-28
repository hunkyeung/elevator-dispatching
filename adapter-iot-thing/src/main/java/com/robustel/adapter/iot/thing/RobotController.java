package com.robustel.adapter.iot.thing;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.domain.elevator.ElevatorArrivedEvent;
import com.robustel.dispatching.domain.robot.RobotId;
import com.robustel.thing.application.ExecutingInstructionApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;

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

    public void enter(RobotId robotId) {
        log.debug("请机器人【{}】进梯", robotId);
        executingInstructionApplication.doExecuteInstruction(robotId.getValue(), "enter", new HashMap<>());
    }

    public void leave(RobotId robotId) {
        log.debug("请机器人【{}】出梯", robotId);
        executingInstructionApplication.doExecuteInstruction(robotId.getValue(), "leave", new HashMap<>());
    }

    @Subscribe
    public void listenOn(ElevatorArrivedEvent event) {
        if (event.isEnterOrLeave()) {
            enter(event.getRobotId());
        } else {
            leave(event.getRobotId());
        }
    }
}
