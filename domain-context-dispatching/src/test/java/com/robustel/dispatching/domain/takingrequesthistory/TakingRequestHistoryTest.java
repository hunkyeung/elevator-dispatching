package com.robustel.dispatching.domain.takingrequesthistory;

import com.robustel.dispatching.domain.InitServiceLocator;
import com.robustel.dispatching.domain.elevator.TakingRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class TakingRequestHistoryTest {
    @BeforeAll
    static void initAll() {
        InitServiceLocator.init();
    }

    @Test
    void Given_Normal_When_New_Then_GetWhatYouSet() {
        TakingRequestHistory takingRequestHistory = new TakingRequestHistory(1L, mock(TakingRequest.class), 1L, Instant.EPOCH);
        assertEquals(1L, takingRequestHistory.id());
        assertNotNull(takingRequestHistory.getTakingRequest());
        assertEquals(1L, takingRequestHistory.getElevatorId());
        assertEquals(Instant.EPOCH, takingRequestHistory.getFinishedOn());
    }

    @Test
    void Given_Null_When_Create_Then_ThrowsException() {
        assertThrows(Exception.class,
                () -> TakingRequestHistory.create(null, 1L));
        assertThrows(Exception.class,
                () -> TakingRequestHistory.create(mock(TakingRequest.class), null));
    }

    @Test
    void Given_Normal_When_Create_Then_GetExpected() {
        TakingRequestHistory of = TakingRequestHistory.create(mock(TakingRequest.class), 1L);
        assertNotNull(of.id());
        assertNotNull(of.getTakingRequest());
        assertEquals(1L, of.getElevatorId());
        assertEquals(Instant.now().truncatedTo(ChronoUnit.SECONDS), of.getFinishedOn().truncatedTo(ChronoUnit.SECONDS));
    }

}