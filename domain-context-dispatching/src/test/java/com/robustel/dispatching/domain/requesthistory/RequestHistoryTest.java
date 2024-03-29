package com.robustel.dispatching.domain.requesthistory;

import com.robustel.ddd.service.EventPublisher;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.ddd.service.UidGenerator;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.elevator.Passenger;
import com.robustel.dispatching.domain.elevator.Request;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class RequestHistoryTest {

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
    void Given_Normal_When_New_Then_GetWhatYouSet() {
        RequestHistory requestHistory = new RequestHistory(1L, mock(Request.class), 1L, Instant.EPOCH);
        assertEquals(1L, requestHistory.id());
        assertNotNull(requestHistory.getRequest());
        assertEquals(1L, requestHistory.getElevatorId());
        assertEquals(Instant.EPOCH, requestHistory.getArchivedOn());
    }

    @Test
    void Given_Null_When_Create_Then_ThrowsException() {
        assertThrows(Exception.class,
                () -> RequestHistory.create(null, 1L));
        assertThrows(Exception.class,
                () -> RequestHistory.create(mock(Request.class), null));
    }

    @Test
    void Given_Normal_When_Create_Then_GetExpected() {
        RequestHistory of = RequestHistory.create(mock(Request.class), 1L);
        assertNotNull(of.id());
        assertNotNull(of.getRequest());
        assertEquals(1L, of.getElevatorId());
        assertEquals(Instant.now().truncatedTo(ChronoUnit.SECONDS), of.getArchivedOn().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    void testToData() {
        RequestHistory requestHistory = new RequestHistory(
                1L,
                new Request(1L, Passenger.of("1"), Floor.of(1), Floor.of(2), Instant.EPOCH, Instant.EPOCH, null, ""),
                1L, Instant.EPOCH);
        RequestHistory.Data data = requestHistory.toData();
        assertEquals(new RequestHistory.Data(1, 1, "1", 1, 2, "1970-01-01 08:00:00", "1970-01-01 08:00:00", "", "", 1, "1970-01-01 08:00:00"), data);
    }

}