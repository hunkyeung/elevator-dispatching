package com.robustel.adapter.iot.thing;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.application.ArrivingFloorApplication;
import com.robustel.dispatching.domain.elevator.Direction;
import com.robustel.dispatching.domain.elevator.ElevatorId;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.rule.domain.matched_event.MatchedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author YangXuehong
 * @date 2022/4/13
 */
@Component
@Slf4j
public class ArrivingFloorListener {
    private final ArrivingFloorApplication arrivingFloorApplication;

    public ArrivingFloorListener(ArrivingFloorApplication arrivingFloorApplication) {
        this.arrivingFloorApplication = arrivingFloorApplication;
    }

    @Subscribe
    public void arrive(MatchedEvent event) {
        if (event.getFact().get("uri").equals(".edge.arriving_event")) {
            Map properties = ((Map) event.getFact().get("properties"));
            Direction direction = Direction.valueOf((String) properties.get("direction"));
            int floor = Integer.valueOf((String) properties.get("floor"));
            String status = "";
            if (Direction.DOWN.equals(direction)) {
                status = "下行";
            } else if (Direction.UP.equals(direction)) {
                status = "上行";
            } else {

            }
            log.info("电梯【{}】{}，已到达{}楼", event.getInstanceId(), status, floor);
            arrivingFloorApplication.doArrive(ElevatorId.of(event.getInstanceId()), Floor.of(floor), direction);
        }
    }
}
