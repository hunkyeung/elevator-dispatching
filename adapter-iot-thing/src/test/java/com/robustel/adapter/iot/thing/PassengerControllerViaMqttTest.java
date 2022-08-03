package com.robustel.adapter.iot.thing;

import com.robustel.dispatching.domain.elevator.Passenger;
import com.robustel.thing.application.ExecutingInstructionApplication;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PassengerControllerViaMqttTest {

    @Test
    void testPleaseIn() {
        var application = mock(ExecutingInstructionApplication.class);
        var passengerControllerViaMqtt = new PassengerControllerViaMqtt(application);
        passengerControllerViaMqtt.pleaseIn(Passenger.of("1"));
        verify(application).doExecuteInstruction("1", "enter", Map.of());
    }

    @Test
    void testPleaseOut() {
        var application = mock(ExecutingInstructionApplication.class);
        var passengerControllerViaMqtt = new PassengerControllerViaMqtt(application);
        passengerControllerViaMqtt.pleaseOut(Passenger.of("1"));
        verify(application).doExecuteInstruction("1", "leave", Map.of());
    }

}