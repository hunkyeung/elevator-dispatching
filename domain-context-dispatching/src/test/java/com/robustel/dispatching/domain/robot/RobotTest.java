package com.robustel.dispatching.domain.robot;

import com.robustel.dispatching.domain.InitServiceLocator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RobotTest {

    @BeforeAll
    static void initAll() {
        InitServiceLocator.init();
    }

    @Test
    void Given_Null_When_Of_Then_ThrowException() {
        assertThrows(NullPointerException.class,
                () -> Robot.create(null, "bar"));
        assertThrows(NullPointerException.class,
                () -> Robot.create("foo", null));
    }

    @Test
    void Given_Normal_When_Of_Then_YouExpected() {
        Robot of = Robot.create("foo", "bar");
        assertNotNull(of.id());
        assertEquals("foo", of.getName());
    }

}