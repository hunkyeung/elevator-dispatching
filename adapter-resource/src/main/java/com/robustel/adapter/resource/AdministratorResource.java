package com.robustel.adapter.resource;

import com.robustel.dispatching.application.*;
import com.robustel.dispatching.domain.elevator.Direction;
import com.robustel.dispatching.domain.elevator.Floor;
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
    private BindingAndUnbindingPassengerApplication bindingAndUnbindingPassengerApplication;
    @Autowired
    private ReleaseTheDoorApplication releaseTheDoorApplication;

    @Autowired
    private OpeningTheDoorApplication openingTheDoorApplication;

    @PostMapping("/robots")
    public RestResponse<Map<String, Object>> registerRobot(@RequestBody RegisteringRobotApplication.Command command) {
        return RestResponse.ofSuccess(Map.of("robotId", registeringRobotApplication.doRegister(command)));
    }

    @PostMapping("/elevators")
    public RestResponse<Map<String, Object>> registerElevator(@RequestBody RegisteringElevatorApplication.Command command) {
        return RestResponse.ofSuccess(Map.of("elevatorId", registeringElevatorApplication.doRegister(command)));
    }

    @PutMapping("/elevators/{elevatorId}/doors")
    public RestResponse<Void> openDoor(@PathVariable Long elevatorId, @RequestParam Floor floor, @RequestParam Direction nextDirection) {
        openingTheDoorApplication.doOpenDoor(elevatorId, floor, nextDirection);
        return RestResponse.ofSuccessWithoutResult();
    }

    @DeleteMapping("/elevators/{elevatorId}/doors")
    public RestResponse<Void> releaseDoor(@PathVariable Long elevatorId) {
        releaseTheDoorApplication.doReleaseDoor(elevatorId);
        return RestResponse.ofSuccessWithoutResult();
    }

    @PutMapping("/elevators/{elevatorId}/binding")
    public RestResponse<Void> bindToElevator(@PathVariable Long elevatorId, @RequestParam String passengerId) {
        bindingAndUnbindingPassengerApplication.doBind(elevatorId, passengerId);
        return RestResponse.ofSuccessWithoutResult();
    }

    @DeleteMapping("/elevators/{elevatorId}/binding")
    public RestResponse<Void> unbindFromElevator(@PathVariable Long elevatorId, @RequestParam String passengerId) {
        bindingAndUnbindingPassengerApplication.doUnbind(elevatorId, passengerId);
        return RestResponse.ofSuccessWithoutResult();
    }
}
