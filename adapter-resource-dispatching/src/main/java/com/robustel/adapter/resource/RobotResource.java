package com.robustel.adapter.resource;

import com.robustel.dispatching.application.FinishingEnteringElevatorApplication;
import com.robustel.dispatching.application.FinishingLeavingElevatorApplication;
import com.robustel.dispatching.application.ReleasingDoorApplication;
import com.robustel.dispatching.application.TakingElevatorApplication;
import com.robustel.dispatching.domain.elevator.ElevatorId;
import com.robustel.dispatching.domain.robot.RobotId;
import com.robustel.utils.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author YangXuehong
 * @date 2022/4/11
 */
@RestController
@RequestMapping("/dispatching")
public class RobotResource {
    @Autowired
    private TakingElevatorApplication takingElevatorApplication;
    @Autowired
    private FinishingEnteringElevatorApplication finishingEnteringElevatorApplication;
    @Autowired
    private FinishingLeavingElevatorApplication finishingLeavingElevatorApplication;
    @Autowired
    private ReleasingDoorApplication releasingDoorApplication;

    @PutMapping("/robots/{robotId}/taking")
    public RestResponse<Map<String, Object>> takeElevator(@PathVariable String robotId, @RequestBody TakingElevatorApplication.Command command) {
        Map<String, Object> results = new HashMap<>();
        results.put("elevatorId", takingElevatorApplication.doTakeElevator(RobotId.of(robotId), command));
        return RestResponse.ofSuccess(results);
    }

    @PutMapping("/robots/{robotId}/finishing-entering")
    public RestResponse<Void> enterElevator(@PathVariable String robotId, @RequestParam String elevatorId) {
        finishingEnteringElevatorApplication.doFinishEnteringElevator(RobotId.of(robotId), ElevatorId.of(elevatorId));
        return RestResponse.ofSuccessWithoutResult();
    }

    @PutMapping("/robots/{robotId}/finishing-leaving")
    public RestResponse<Void> leaveElevator(@PathVariable String robotId, @RequestParam String elevatorId) {
        finishingLeavingElevatorApplication.doFinishLeavingElevator(RobotId.of(robotId), ElevatorId.of(elevatorId));
        return RestResponse.ofSuccessWithoutResult();
    }

    @PutMapping("/robots/{robotId}/releasing")
    public RestResponse<Void> releaseDoor(@PathVariable String robotId, @RequestParam String elevatorId) {
        releasingDoorApplication.doReleaseDoor(RobotId.of(robotId), ElevatorId.of(elevatorId));
        return RestResponse.ofSuccessWithoutResult();
    }
}
