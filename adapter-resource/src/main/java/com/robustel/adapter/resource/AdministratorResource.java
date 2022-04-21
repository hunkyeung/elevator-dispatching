package com.robustel.adapter.resource;

import com.robustel.dispatching.application.BindingAndUnbindingElevatorApplication;
import com.robustel.dispatching.application.RegisteringElevatorApplication;
import com.robustel.dispatching.application.RegisteringRobotApplication;
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
public class AdministratorResource {
    @Autowired
    private RegisteringRobotApplication registeringRobotApplication;
    @Autowired
    private RegisteringElevatorApplication registeringElevatorApplication;
    @Autowired
    private BindingAndUnbindingElevatorApplication bindingAndUnbindingElevatorApplication;

    @PostMapping("/robots")
    public RestResponse<Map<String, Object>> registerRobot(@RequestBody RegisteringRobotApplication.Command command) {
        Map<String, Object> results = new HashMap<>();
        results.put("robotId", registeringRobotApplication.doRegister(command));
        return RestResponse.ofSuccess(results);
    }

    @PostMapping("/elevators")
    public RestResponse<Map<String, Object>> registerElevator(@RequestBody RegisteringElevatorApplication.Command command) {
        Map<String, Object> results = new HashMap<>();
        results.put("elevatorId", registeringElevatorApplication.doRegister(command));
        return RestResponse.ofSuccess(results);
    }


    @PutMapping("/robots/{robotId}/binding")
    public void bindElevator(@PathVariable String robotId, @RequestParam String elevatorId) {
        bindingAndUnbindingElevatorApplication.doBindElevator(RobotId.of(robotId), ElevatorId.of(elevatorId));
    }

    @PutMapping("/robots/{robotId}/unbinding")
    public void unbindElevator(@PathVariable String robotId, @RequestParam String elevatorId) {
        bindingAndUnbindingElevatorApplication.doUnbindElevator(RobotId.of(robotId), ElevatorId.of(elevatorId));
    }
}
