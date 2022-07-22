package com.robustel.adapter.iot.thing;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.application.ArrivingTheFloorApplication;
import com.robustel.dispatching.domain.elevator.Direction;
import com.robustel.dispatching.domain.elevator.ElevatorController;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.rule.domain.matched_event.MatchedEvent;
import com.robustel.thing.application.ExecutingInstructionApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author YangXuehong
 * @date 2022/4/12
 */
@Component
@Slf4j
public class ElevatorControllerViaMqtt implements ElevatorController {
    @Value("${robustel.elevator-dispatching.app-name}")
    private String appName;
    private final ExecutingInstructionApplication executingInstructionApplication;
    private final ArrivingTheFloorApplication arrivingTheFloorApplication;

    public ElevatorControllerViaMqtt(ExecutingInstructionApplication executingInstructionApplication, ArrivingTheFloorApplication arrivingTheFloorApplication) {
        this.executingInstructionApplication = executingInstructionApplication;
        this.arrivingTheFloorApplication = arrivingTheFloorApplication;
    }

    @Override
    public void lightUp(long elevatorId, Floor floor) {
        Map<String, Object> params = Map.of("floor", floor.getValue());
        executingInstructionApplication.doExecuteInstruction(
                String.valueOf(elevatorId),
                "take", params);
        log.debug("点亮电梯【{}】第【{}】层按钮", elevatorId, floor);
    }

    @Override
    public void release(long elevatorId) {
        executingInstructionApplication.doExecuteInstruction(
                String.valueOf(elevatorId),
                "release", Map.of());
    }

    @Subscribe
    public void listenOn(MatchedEvent event) {
        // 当电梯开门时
        if (event.getFact().get("uri").equals("." + appName + ".arriving_event")) {
            Map<String, Object> properties = ((Map) event.getFact().get("properties"));
            int floor = Integer.parseInt((String) properties.get("floor"));
            Direction direction = Direction.valueOf((String) properties.get("direction"));
            log.debug("电梯【{}】已到达{}楼", event.getInstanceId(), floor);
            arrivingTheFloorApplication.doArrive(Long.valueOf(event.getInstanceId()), Floor.of(floor), direction);
        }
    }
}
