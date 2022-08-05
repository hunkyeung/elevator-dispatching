package com.robustel.adapter.iot.thing;

import com.robustel.dispatching.application.ArrivingTheFloorApplication;
import com.robustel.dispatching.domain.elevator.Direction;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.rule.domain.matched_event.MatchedEvent;
import com.robustel.thing.application.ExecutingInstructionApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ElevatorControllerViaMqttTest {

    private ExecutingInstructionApplication executingInstructionApplication;
    private ArrivingTheFloorApplication arrivingTheFloorApplication;
    private ElevatorControllerViaMqtt elevatorControllerViaMqtt;

    @BeforeEach
    void init() {
        executingInstructionApplication = mock(ExecutingInstructionApplication.class);
        arrivingTheFloorApplication = mock(ArrivingTheFloorApplication.class);
        elevatorControllerViaMqtt = new ElevatorControllerViaMqtt(executingInstructionApplication, arrivingTheFloorApplication);
        elevatorControllerViaMqtt.setPress("press");
        elevatorControllerViaMqtt.setRelease("release");
        elevatorControllerViaMqtt.setEventResource(".e2c_s_ladder_control_rs485.arriving_event");
    }

    @Test
    void testPress() {
        elevatorControllerViaMqtt.press(1L, Floor.of(1));
        verify(executingInstructionApplication).doExecuteInstruction("1", elevatorControllerViaMqtt.getPress(), Map.of("floor", Floor.of(1)));
    }

    @Test
    void testBatchPress() {
        elevatorControllerViaMqtt.press(1L, Set.of(Floor.of(1), Floor.of(2)));
        verify(executingInstructionApplication).doExecuteInstruction(eq("1"), eq(elevatorControllerViaMqtt.getPress()), any());
    }

    @Test
    void testBatchPressWithEmptySet() {
        elevatorControllerViaMqtt.press(1L, Set.of());
        verify(executingInstructionApplication, never()).doExecuteInstruction(any(), any(), any());
    }

    @Test
    void testRelease() {
        elevatorControllerViaMqtt.release(1L);
        verify(executingInstructionApplication).doExecuteInstruction("1", elevatorControllerViaMqtt.getRelease(), Map.of());
    }

    @Test
    void testListenOnWithEmptyMap() {
        MatchedEvent matchedEvent = new MatchedEvent("foo", "bar", "foobar2000", Map.of());
        elevatorControllerViaMqtt.listenOn(matchedEvent);
        verify(arrivingTheFloorApplication, never()).doArrive(any(), any(), any());
    }

    @Test
    void testListenOn() {
        MatchedEvent matchedEvent = new MatchedEvent("foo", "2000", "foobar2000",
                Map.of("uri", ".e2c_s_ladder_control_rs485.arriving_event",
                        "reportAt", "1640131885515",
                        "properties", Map.of("floor", "-1", "direction", "DOWN")
                ));
        elevatorControllerViaMqtt.listenOn(matchedEvent);
        verify(arrivingTheFloorApplication).doArrive(2000L, Floor.of(-1), Direction.DOWN);
    }

}