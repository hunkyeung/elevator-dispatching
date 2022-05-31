package com.robustel.adapter.resource;

import com.robustel.dispatching.application.*;
import com.robustel.utils.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    private ResettingElevatorApplication resettingElevatorApplication;
    @Autowired
    private BindingAndUnbindingElevatorApplication bindingAndUnbindingElevatorApplication;
    @Autowired
    private TellingPassengerOutInApplication tellingPassengerOutInApplication;

    @PostMapping("/robots")
    public RestResponse<Map<String, Object>> registerRobot(@RequestBody RegisteringRobotApplication.Command command) {
        return RestResponse.ofSuccess(Map.of("robotId", registeringRobotApplication.doRegister(command)));
    }

    @PostMapping("/elevators")
    public RestResponse<Map<String, Object>> registerElevator(@RequestBody RegisteringElevatorApplication.Command command) {
        return RestResponse.ofSuccess(Map.of("elevatorId", registeringElevatorApplication.doRegister(command)));
    }

    @PutMapping("/elevators/{elevatorId}")
    public RestResponse<Void> resetElevator(@PathVariable Long elevatorId) {
        resettingElevatorApplication.doResetElevator(elevatorId);
        return RestResponse.ofSuccessWithoutResult();
    }


    @PutMapping("/elevators/{elevatorId}/binding")
    public RestResponse<Void> bindToElevator(@PathVariable Long elevatorId, @RequestParam Long robotId) {
        bindingAndUnbindingElevatorApplication.doBindToElevator(elevatorId, robotId);
        return RestResponse.ofSuccessWithoutResult();
    }

    @DeleteMapping("/elevators/{elevatorId}/binding")
    public RestResponse<Void> unbindFromElevator(@PathVariable Long elevatorId, @RequestParam Long robotId) {
        bindingAndUnbindingElevatorApplication.doUnbindFromElevator(elevatorId, robotId);
        return RestResponse.ofSuccessWithoutResult();
    }
}
