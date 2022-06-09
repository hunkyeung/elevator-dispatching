package com.robustel.dispatching.domain.requesthistory;

import com.robustel.dispatching.domain.InitServiceLocator;
import com.robustel.dispatching.domain.elevator.Request;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RequestHistoryTest {
    @BeforeAll
    static void initAll() {
        InitServiceLocator.init();
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

}