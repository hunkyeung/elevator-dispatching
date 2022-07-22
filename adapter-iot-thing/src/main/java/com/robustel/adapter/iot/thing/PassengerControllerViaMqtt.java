package com.robustel.adapter.iot.thing;

import com.robustel.dispatching.domain.elevator.Passenger;
import com.robustel.dispatching.domain.elevator.PassengerController;
import com.robustel.thing.application.ExecutingInstructionApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author YangXuehong
 * @date 2022/4/13
 */
@Component
@Slf4j
public class PassengerControllerViaMqtt implements PassengerController {
    private final ExecutingInstructionApplication executingInstructionApplication;

    public PassengerControllerViaMqtt(ExecutingInstructionApplication executingInstructionApplication) {
        this.executingInstructionApplication = executingInstructionApplication;
    }

    @Override
    public void pleaseOut(Passenger passenger) {
        log.debug("请乘客【{}】出梯", passenger);
        executingInstructionApplication.doExecuteInstruction(passenger.getId(), "leave", Map.of());
    }

    @Override
    public void pleaseIn(Passenger passenger) {
        log.debug("请乘客【{}】进梯", passenger);
        executingInstructionApplication.doExecuteInstruction(passenger.getId(), "enter", Map.of());
    }
}
