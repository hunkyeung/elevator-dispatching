package com.robustel.dispatching.domain.elevator;

import com.codebox.bean.JavaBeanTester;
import com.robustel.dispatching.domain.InitServiceLocator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ElevatorRegisteredEventTest {
    @BeforeAll
    static void initAll() {
        InitServiceLocator.init();
    }

    @Test
    void test() {
        ElevatorRegisteredEvent elevatorRegisteredEvent = new ElevatorRegisteredEvent(1L, "foo", "bar");
        assertEquals(1L, elevatorRegisteredEvent.getElevatorId());
        assertEquals("foo", elevatorRegisteredEvent.getModelId());
        assertEquals("bar", elevatorRegisteredEvent.getSn());
    }

}