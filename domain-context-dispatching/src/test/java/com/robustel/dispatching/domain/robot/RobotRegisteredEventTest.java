package com.robustel.dispatching.domain.robot;

import com.robustel.ddd.service.ServiceLocator;
import com.robustel.ddd.service.UidGenerator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RobotRegisteredEventTest {

    public static final MockedStatic<ServiceLocator> MOCKED_STATIC = mockStatic(ServiceLocator.class);

    @BeforeAll
    static void initAll() {
        MOCKED_STATIC.when(() -> ServiceLocator.service(UidGenerator.class)).thenReturn((UidGenerator) () -> 1);
    }

    @AfterAll
    static void close() {
        MOCKED_STATIC.close();
    }

    @Test
    void test() {
        RobotRegisteredEvent robotRegisteredEvent = new RobotRegisteredEvent(1L, "foobar2000");
        assertEquals(1L, robotRegisteredEvent.getRobotId());
        assertEquals("foobar2000", robotRegisteredEvent.getModelId());
        assertEquals(1L, robotRegisteredEvent.id());
    }

}