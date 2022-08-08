package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.DomainException;
import com.robustel.ddd.service.EventPublisher;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.ddd.service.UidGenerator;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RequestTest {

    private Request of;

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


    @BeforeEach
    void init() {
        of = Request.create(Passenger.of("1"), Floor.of(-1), Floor.of(2));
    }

    @Test
    void Given_Null_When_Create_Then_ThrowException() {
        Floor floor_1 = Floor.of(-1);
        Floor floor2 = Floor.of(2);
        assertThrows(NullPointerException.class,
                () -> Request.create(null, floor_1, floor2));
        Passenger passenger = Passenger.of("1");
        assertThrows(NullPointerException.class,
                () -> Request.create(passenger, null, floor2));
        assertThrows(NullPointerException.class,
                () -> Request.create(passenger, floor_1, null));
        assertThrows(DomainException.class,
                () -> Request.create(passenger, floor_1, floor_1));
    }

    @Test
    void Given_Cause_When_Cancel_Then_CauseWasSet() {
        assertNull(of.getStatus());
        of.cancel("Fail");
        assertEquals("Fail", of.getStatus());
    }

    @Test
    void Given_RequestFrom1To5AndCurrentFloor5_When_ShouldOut_Then_ReturnTrue() {
        Request request = new Request(1L, Passenger.of("1"), Floor.of(1), Floor.of(5), Instant.now(), Instant.now(), null, null);
        assertTrue(request.shouldOut(Floor.of(5)));
    }

    @Test
    void Given_RequestFrom1To5AndCurrentFloor5ButNotIn_When_ShouldOut_Then_ReturnFalse() {
        Request request = new Request(1L, Passenger.of("1"), Floor.of(1), Floor.of(5), Instant.now(), null, null, null);
        assertFalse(request.shouldOut(Floor.of(5)));
    }

    @Test
    void Given_RequestFrom1To5AndCurrentFloor4_When_ShouldOut_Then_ReturnFalse() {
        Request request = new Request(1L, Passenger.of("1"), Floor.of(1), Floor.of(5), Instant.now(), Instant.now(), null, null);
        assertFalse(request.shouldOut(Floor.of(4)));
    }


    @Test
    void Given_RequestFrom1To5AndCurrentFloor1AndDirectionUp_When_ShouldIn_Then_ReturnTrue() {
        Request request = new Request(1L, Passenger.of("1"), Floor.of(1), Floor.of(5), Instant.now(), null, null, null);
        assertTrue(request.shouldIn(Floor.of(1), Direction.UP));
    }

    @Test
    void Given_RequestFrom1To5AndCurrentFloor1AndDirectionStop_When_ShouldIn_Then_ReturnTrue() {
        Request request = new Request(1L, Passenger.of("1"), Floor.of(1), Floor.of(5), Instant.now(), null, null, null);
        assertTrue(request.shouldIn(Floor.of(1), Direction.STOP));
    }

    @Test
    @Disabled("since ignore direction")
    void Given_RequestFrom1To5AndCurrentFloor1AndDirectionDown_When_ShouldIn_Then_ReturnTrue() {
        Request request = new Request(1L, Passenger.of("1"), Floor.of(1), Floor.of(5), Instant.now(), null, null, null);
        assertFalse(request.shouldIn(Floor.of(1), Direction.DOWN));
    }

    @Test
    void Given_RequestFrom5To1AndCurrentFloor1AndDirectionDown_When_ShouldIn_Then_ReturnTrue() {
        Request request = new Request(1L, Passenger.of("1"), Floor.of(5), Floor.of(1), Instant.now(), null, null, null);
        assertTrue(request.shouldIn(Floor.of(5), Direction.DOWN));
    }

    @Test
    void Given_RequestFrom5To1AndCurrentFloor1AndDirectionStop_When_ShouldIn_Then_ReturnTrue() {
        Request request = new Request(1L, Passenger.of("1"), Floor.of(5), Floor.of(1), Instant.now(), null, null, null);
        assertTrue(request.shouldIn(Floor.of(5), Direction.STOP));
    }

    @Test
    @Disabled("since ignore direction")
    void Given_RequestFrom5To1AndCurrentFloor1AndDirectionUp_When_ShouldIn_Then_ReturnTrue() {
        Request request = new Request(1L, Passenger.of("1"), Floor.of(5), Floor.of(1), Instant.now(), null, null, null);
        assertFalse(request.shouldIn(Floor.of(5), Direction.UP));
    }

    @Test
    void Given_RequestFrom5To1AndCurrentFloorNotMatchFrom_When_ShouldIn_Then_ReturnFalse() {
        Request request = new Request(1L, Passenger.of("1"), Floor.of(5), Floor.of(1), Instant.now(), null, null, null);
        assertFalse(request.shouldIn(Floor.of(2), Direction.UP));
        assertFalse(request.shouldIn(Floor.of(3), Direction.DOWN));
        assertFalse(request.shouldIn(Floor.of(4), Direction.STOP));
    }

}