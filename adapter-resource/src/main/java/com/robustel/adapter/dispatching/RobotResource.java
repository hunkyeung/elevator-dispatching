package com.robustel.adapter.dispatching;

import com.robustel.dispatching.application.CancelingRequestApplication;
import com.robustel.dispatching.application.FinishingApplication;
import com.robustel.dispatching.application.TakingElevatorApplication;
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
public class RobotResource {
    @Autowired
    private TakingElevatorApplication takingElevatorApplication;
    @Autowired
    private FinishingApplication finishingApplication;
    @Autowired
    private CancelingRequestApplication cancelingRequestApplication;

    @PostMapping("/requests")
    public RestResponse<Map<String, Object>> takeElevator(@RequestBody TakingElevatorApplication.Command command) {
        return RestResponse.ofSuccess(Map.of("elevatorId", takingElevatorApplication.doTakeElevator(command)));
    }

    @PutMapping("/elevators/{elevatorId}/requests")
    public RestResponse<Void> finish(@PathVariable Long elevatorId, @RequestBody FinishingApplication.Command command) {
        finishingApplication.doFinish(elevatorId, command);
        return RestResponse.ofSuccessWithoutResult();
    }

    @DeleteMapping("/elevators/{elevatorId}/requests")
    public RestResponse<Void> cancelRequest(@PathVariable Long elevatorId, @RequestBody CancelingRequestApplication.Command command) {
        cancelingRequestApplication.doCancelRequest(elevatorId, command);
        return RestResponse.ofSuccessWithoutResult();
    }
}
