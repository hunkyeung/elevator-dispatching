package com.robustel.adapter.iot.thing;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.application.OpeningTheDoorApplication;
import com.robustel.dispatching.domain.elevator.Direction;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.rule.domain.matched_event.MatchedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author YangXuehong
 * @date 2022/4/13
 */
@Component
@Slf4j
public class ArrivingFloorListener {
    @Value("${robustel.elevator-dispatching.app-name}")
    private String appName;

    private final OpeningTheDoorApplication openingTheDoorApplication;

    public ArrivingFloorListener(OpeningTheDoorApplication openingTheDoorApplication) {
        this.openingTheDoorApplication = openingTheDoorApplication;
    }

    @Subscribe
    public void arrive(MatchedEvent event) {
        if (event.getFact().get("uri").equals("." + appName + ".arriving_event")) {
            Map<String,Object> properties = ((Map) event.getFact().get("properties"));
            int floor = Integer.parseInt((String) properties.get("floor"));
            Direction direction = Direction.valueOf((String) properties.get("direction"));
            log.debug("电梯【{}】已到达{}楼", event.getInstanceId(), floor);
            openingTheDoorApplication.doOpenDoor(Long.valueOf(event.getInstanceId()), Floor.of(floor), direction);
        }
    }
}
