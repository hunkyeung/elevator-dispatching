package com.robustel.adapter.iot.thing;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.application.ArrivingTheFloorApplication;
import com.robustel.dispatching.domain.elevator.Direction;
import com.robustel.dispatching.domain.elevator.ElevatorController;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.rule.domain.matched_event.MatchedEvent;
import com.robustel.thing.application.ExecutingInstructionApplication;
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
    public static final String FLOOR = "floor";
    public static final String DIRECTION = "direction";
    public static final String PROPERTIES = "properties";
    public static final String URI = "uri";
    @Value("${robustel.elevator-dispatching.elevator.event-resource}")
    private String eventResource;
    @Value("${robustel.elevator-dispatching.elevator.instruction.press}")
    private String press;
    @Value("${robustel.elevator-dispatching.elevator.instruction.release}")
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
        log.debug("????????????{}?????????{}????????????", elevatorId, floor);
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
        log.debug("????????????{}?????????{}????????????", elevatorId, floors);
    }

    @Subscribe
    public void listenOn(MatchedEvent event) {
        // ??????????????????
        if (event.getFact().get(URI).equals(eventResource)) {
            Map<String, Object> properties = ((Map) event.getFact().get(PROPERTIES));
            var floor = Integer.parseInt((String) properties.get(FLOOR));
            var direction = Direction.valueOf((String) properties.get(DIRECTION));
            log.debug("?????????{}????????????{}???", event.getInstanceId(), floor);
            arrivingTheFloorApplication.doArrive(Long.valueOf(event.getInstanceId()), Floor.of(floor), direction);
        }
    }
}
