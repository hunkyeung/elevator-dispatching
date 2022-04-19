package com.robustel.adapter.resource;

import com.robustel.dispatching.application.RegisteringElevatorApplication;
import com.robustel.dispatching.application.RegisteringRobotApplication;
import com.robustel.utils.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
