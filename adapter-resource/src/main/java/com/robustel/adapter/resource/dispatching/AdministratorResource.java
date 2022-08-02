package com.robustel.adapter.resource.dispatching;

import com.alibaba.fastjson.JSON;
import com.robustel.ddd.query.Page;
import com.robustel.ddd.query.PageResult;
import com.robustel.dispatching.application.*;
import com.robustel.dispatching.domain.elevator.Direction;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.requesthistory.RequestHistory;
import com.robustel.thing.application.ExecutingInstructionApplication;
import com.robustel.utils.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

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
    private UnregisteringElevatorApplication unregisteringElevatorApplication;
    @Autowired
    private BindingAndUnbindingPassengerApplication bindingAndUnbindingPassengerApplication;
    @Autowired
    private ReleasingElevatorApplication releaseTheDoorApplication;
    @Autowired
    private ArrivingTheFloorApplication arrivingTheFloorApplication;
    @Autowired
    private ExecutingInstructionApplication executingInstructionApplication;
    @Autowired
    private GettingRequestHistoryApplication gettingRequestHistoryApplication;

    @PostMapping("/robots")
    public RestResponse<Map<String, Object>> registerRobot(@RequestBody RegisteringRobotApplication.Command command) {
        return RestResponse.ofSuccess(Map.of("robotId", registeringRobotApplication.doRegister(command)));
    }

    @PostMapping("/elevators")
    public RestResponse<Map<String, Object>> registerElevator(@RequestBody RegisteringElevatorApplication.Command command) {
        return RestResponse.ofSuccess(Map.of("elevatorId", registeringElevatorApplication.doRegister(command)));
    }

    @DeleteMapping("/elevators/{elevatorId}")
    public RestResponse<Void> unregisterElevator(@PathVariable Long elevatorId) {
        unregisteringElevatorApplication.doUnregister(elevatorId);
        return RestResponse.ofSuccessWithoutResult();
    }

    @PostMapping("/elevators/commands")
    public RestResponse<String> executeCommand(@RequestBody Map<String, Object> body) {
        String elevatorId = (String) body.get("elevatorId");
        String commandName = (String) body.get("commandName");
        String parameter = (String) body.get("parameter");
        return RestResponse.ofSuccess(
                executingInstructionApplication.doExecuteInstruction(String.valueOf(elevatorId), commandName, JSON.parseObject(parameter, Map.class)).getId()
        );
    }


    @PutMapping("/elevators/{elevatorId}/doors")
    public RestResponse<Void> openDoor(@PathVariable Long elevatorId, @RequestParam Floor floor, @RequestParam Direction nextDirection) {
        arrivingTheFloorApplication.doArrive(elevatorId, floor, nextDirection);
        return RestResponse.ofSuccessWithoutResult();
    }


    @DeleteMapping("/elevators/{elevatorId}/doors")
    public RestResponse<Void> releaseDoor(@PathVariable Long elevatorId) {
        releaseTheDoorApplication.doReleaseElevator(elevatorId);
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

    @GetMapping("/request-histories")
    public RestResponse<PageResult<RequestHistory.Data>> getRequestHistory(
            @RequestParam(required = false) Long elevatorId, @RequestParam(required = false) String passenger,
            @RequestParam(required = false) Integer pageSize, @RequestParam(required = false) Integer pageNum) {
        if (Objects.isNull(pageNum)) {
            pageNum = 1;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = 20;
        }
        return RestResponse.ofSuccess(gettingRequestHistoryApplication.getRequestHistory(elevatorId, passenger, Page.of(pageSize, pageNum)));
    }
}
