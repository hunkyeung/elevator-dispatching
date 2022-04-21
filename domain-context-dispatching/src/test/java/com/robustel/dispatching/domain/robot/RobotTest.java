package com.robustel.dispatching.domain.robot;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author YangXuehong
 * @date 2022/4/11
 */
class RobotTest {

    @Test
    void Given_Normal_When_Enter_Then_EnteringTimeWasSet() {
        Set<ElevatorId> elevatorIdSet = new HashSet<>();
        ElevatorId elevatorId = ElevatorId.of("1");
        elevatorIdSet.add(elevatorId);
        Robot robot = new Robot(RobotId.of(UUID.randomUUID().toString()), null, null, elevatorIdSet);
        assertNull(robot.getEnteringTime());
        Elevator elevator = mock(Elevator.class);
        when(elevator.getId()).thenReturn(elevatorId);
        robot.enter(elevator);
        assertNotNull(robot.getEnteringTime());
        verify(elevator).enter(robot.getId());
        //todo 需要补充验证设置的时间与当前时间误差在许可范围
    }

    @Test
    void Given_RobotEnteringTheElevator_When_LeaveTheElevator_Then_LeavingTimeWasSet() {
        Robot robot = new Robot(RobotId.of(UUID.randomUUID().toString()), Instant.now(), null, new HashSet<>());
        assertNotNull(robot.getEnteringTime());
        assertNull(robot.getLeavingTime());
        Elevator elevator = mock(Elevator.class);
        robot.leave(elevator);
        verify(elevator).leave(robot.getId());
        assertNotNull(robot.getLeavingTime());
        //todo 需要补充验证设置的时间与当前时间误差在许可范围
    }

    @Test
    void Given_RobotNotEnteringTheElevator_When_LeaveTheElevator_Then_ElevatorLeaveInvoked() {
        Robot robot = new Robot(RobotId.of(UUID.randomUUID().toString()), null, null, new HashSet<>());
        assertNull(robot.getEnteringTime());
        assertNull(robot.getLeavingTime());
        Elevator elevator = mock(Elevator.class);
        robot.leave(elevator);
        verify(elevator).leave(robot.getId());
    }

    @Test
    void Given_Elevator_When_Bind_Then_AddInWhiteListAndElevatorBindInvoked() {
        Robot robot = new Robot(RobotId.of(UUID.randomUUID().toString()), null, null, new HashSet<>());
        Elevator elevator = mock(Elevator.class);
        when(elevator.getId()).thenReturn(ElevatorId.of("1"));
        robot.bind(elevator);
        assertTrue(robot.getWhiteList().contains(elevator.getId()));
        verify(elevator).bind(robot.getId());
    }

    @Test
    void Given_Elevator_When_Unbind_Then_RemoveFromWhiteListAndElevatorUnbindInvoked() {
        Set<ElevatorId> elevatorIdSet = new HashSet<>();
        ElevatorId elevatorId = ElevatorId.of("1");
        elevatorIdSet.add(elevatorId);
        Robot robot = new Robot(RobotId.of(UUID.randomUUID().toString()), null, null, elevatorIdSet);
        Elevator elevator = mock(Elevator.class);
        when(elevator.getId()).thenReturn(elevatorId);
        assertTrue(robot.getWhiteList().contains(elevator.getId()));
        robot.unbind(elevator);
        assertFalse(robot.getWhiteList().contains(elevator.getId()));
        verify(elevator).unbind(robot.getId());
    }

}