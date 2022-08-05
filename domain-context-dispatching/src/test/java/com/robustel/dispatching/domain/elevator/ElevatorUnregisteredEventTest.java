package com.robustel.dispatching.domain.elevator;

import com.robustel.dispatching.domain.InitServiceLocator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ElevatorUnregisteredEventTest {
    @BeforeAll
    static void initAll() {
        InitServiceLocator.init();
    }

    @Test
    void test() {
        ElevatorUnregisteredEvent elevatorUnregisteredEvent = new ElevatorUnregisteredEvent(1L);
        assertEquals(1L, elevatorUnregisteredEvent.getElevatorId());
    }

}