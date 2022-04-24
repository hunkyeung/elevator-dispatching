package com.robustel.dispatching.domain.elevator;

import com.robustel.dispatching.domain.robot.RobotId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author YangXuehong
 * @date 2022/4/11
 */
class RequestTest {

    @Test
    void Given_TheSameFloor_When_of_Then_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Request.of(RobotId.of("1"), Floor.of(1), Floor.of(1)));
    }

    @Test
    void Given_NullRobotId_When_Of_Then_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Request.of(RobotId.of("1"), null, null));
    }

    @Test
    void Given_NullFromOfFloor_When_Of_Then_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Request.of(RobotId.of(UUID.randomUUID().toString()), null, null));
    }

    @Test
    void Given_NullToOfFloor_When_Of_Then_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Request.of(RobotId.of(UUID.randomUUID().toString()), Floor.of(1), null));
    }

    @Test
    void Given_Normal_When_Of_Then_GetRequest() {
        Request request = Request.of(RobotId.of(UUID.randomUUID().toString()), Floor.of(1), Floor.of(2));
        assertNotNull(request.getOccurredOn());
    }

    @Test
    void Given_From2_To10_Request_And_Not10floor_When_MatchTo_Then_ReturnFalse() {
        Request request = Request.of(RobotId.of("robotId"), Floor.of(2), Floor.of(10));
        assertFalse(request.matchFrom(Floor.of(1)));
        assertFalse(request.matchFrom(Floor.of(11)));
    }

    @Test
    void Given_From2_To10_Request_And_10floor_When_MatchTo_Then_ReturnTrue() {
        Request request = Request.of(RobotId.of("robotId"), Floor.of(2), Floor.of(10));
        assertTrue(request.matchTo(Floor.of(10)));
    }

}