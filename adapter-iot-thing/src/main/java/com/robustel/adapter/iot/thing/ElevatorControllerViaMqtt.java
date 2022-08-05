package com.robustel.adapter.iot.thing;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.application.ArrivingTheFloorApplication;
import com.robustel.dispatching.domain.elevator.Direction;
import com.robustel.dispatching.domain.elevator.ElevatorController;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.rule.domain.matched_event.MatchedEvent;
import com.robustel.thing.application.ExecutingInstructionApplication;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * @author YangXuehong
 * @date 2022/4/12
 */
@Component
@Slf4j
public class ElevatorControllerViaMqtt implements ElevatorController {
    private static final String FLOOR = "floor";
    private static final String DIRECTION = "direction";
    private static final String PROPERTIES = "properties";
    private static final String URI = "uri";
    @Value("${robustel.elevator-dispatching.elevator.event-resource}")
    @Getter
    @Setter
    private String eventResource;
    @Value("${robustel.elevator-dispatching.elevator.instruction.press}")
    @Getter
    @Setter
    private String press;
    @Value("${robustel.elevator-dispatching.elevator.instruction.release}")
    @Getter
    @Setter
    private String release;
    private final ExecutingInstructionApplication executingInstructionApplication;
    private final ArrivingTheFloorApplication arrivingTheFloorApplication;

    public ElevatorControllerViaMqtt(ExecutingInstructionApplication executingInstructionApplication, ArrivingTheFloorApplication arrivingTheFloorApplication) {
        this.executingInstructionApplication = executingInstructionApplication;
        this.arrivingTheFloorApplication = arrivingTheFloorApplication;
    }

    @Override
    public void press(long elevatorId, Floor floor) {
        Map<String, Object> params = Map.of(FLOOR, floor);
        executingInstructionApplication.doExecuteInstruction(
                String.valueOf(elevatorId),
                press, params);
        log.debug("按电梯【{}】第【{}】层按钮", elevatorId, floor);
    }

    @Override
    public void release(long elevatorId) {
        executingInstructionApplication.doExecuteInstruction(
                String.valueOf(elevatorId),
                release, Map.of());
    }

    @Override
    public void press(long elevatorId, Set<Floor> pressedFloor) {
        if (pressedFloor.isEmpty()) {
            return;
        }
        var floors = StringUtils.join(pressedFloor, ",");
        Map<String, Object> params = Map.of(FLOOR, floors);
        executingInstructionApplication.doExecuteInstruction(
                String.valueOf(elevatorId),
                press, params);
        log.debug("按电梯【{}】第【{}】层按钮", elevatorId, floors);
    }

    @Subscribe
    public void listenOn(MatchedEvent event) {
        // 当电梯开门时
        if (getEventResource().equals(event.getFact().get(URI))) {
            Map<String, Object> properties = ((Map) event.getFact().get(PROPERTIES));
            var floor = Integer.parseInt((String) properties.get(FLOOR));
            var direction = Direction.valueOf((String) properties.get(DIRECTION));
            log.debug("电梯【{}】已到达{}楼", event.getInstanceId(), floor);
            arrivingTheFloorApplication.doArrive(Long.valueOf(event.getInstanceId()), Floor.of(floor), direction);
        }
    }
}
