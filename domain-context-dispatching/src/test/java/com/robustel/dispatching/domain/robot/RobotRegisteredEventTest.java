package com.robustel.dispatching.domain.robot;

import com.robustel.dispatching.domain.InitServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RobotRegisteredEventTest {

    @BeforeEach
    void init() {
        InitServiceLocator.init();
    }

    @Test
    void test() {
        RobotRegisteredEvent robotRegisteredEvent = new RobotRegisteredEvent(1L, "foobar2000");
        assertEquals(1L, robotRegisteredEvent.getRobotId());
        assertEquals("foobar2000", robotRegisteredEvent.getModelId());
    }

}