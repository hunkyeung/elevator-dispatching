package com.robustel.dispatching.domain.robot;

import com.robustel.dispatching.domain.elevator.Elevator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author YangXuehong
 * @date 2022/4/11
 */
class RobotTest {

    @Test
    void Given_Normal_When_Enter_Then_EnteringTimeWasSet() {
        Robot robot = new Robot(RobotId.of(UUID.randomUUID().toString()));
        assertNull(robot.getEnteringTime());
        Elevator elevator = mock(Elevator.class);
        robot.enter(elevator);
        assertNotNull(robot.getEnteringTime());
        verify(elevator).enter(robot.getId());
        //todo 需要补充验证设置的时间与当前时间误差在许可范围
    }

    @Test
    void Given_RobotEnteringTheElevator_When_LeaveTheElevator_Then_LeavingTimeWasSet() {
        Robot robot = new Robot(RobotId.of(UUID.randomUUID().toString()), Instant.now(), null);
        assertNotNull(robot.getEnteringTime());
        assertNull(robot.getLeavingTime());
        Elevator elevator = mock(Elevator.class);
        robot.leave(elevator);
        verify(elevator).leave(robot.getId());
        assertNotNull(robot.getLeavingTime());
        //todo 需要补充验证设置的时间与当前时间误差在许可范围
    }

    @Test
    void Given_RobotNotEnteringTheElevator_When_LeaveTheElevator_Then_ThrowsIllegalStateException() {
        Robot robot = new Robot(RobotId.of(UUID.randomUUID().toString()), null, null);
        assertNull(robot.getEnteringTime());
        assertNull(robot.getLeavingTime());
        Elevator elevator = mock(Elevator.class);
        Assertions.assertThrows(IllegalStateException.class, () -> robot.leave(elevator));
    }


}