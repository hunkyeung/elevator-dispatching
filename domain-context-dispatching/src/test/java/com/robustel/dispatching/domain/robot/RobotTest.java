package com.robustel.dispatching.domain.robot;

import com.robustel.ddd.service.EventPublisher;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.ddd.service.UidGenerator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RobotTest {

    public static final MockedStatic<ServiceLocator> MOCKED_STATIC = mockStatic(ServiceLocator.class);

    @BeforeAll
    static void initAll() {
        MOCKED_STATIC.when(() -> ServiceLocator.service(UidGenerator.class)).thenReturn((UidGenerator) () -> 1);
        MOCKED_STATIC.when(() -> ServiceLocator.service(EventPublisher.class)).thenReturn(mock(EventPublisher.class));
    }

    @AfterAll
    static void close() {
        MOCKED_STATIC.close();
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