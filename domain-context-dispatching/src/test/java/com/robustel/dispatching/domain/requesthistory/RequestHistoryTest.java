package com.robustel.dispatching.domain.requesthistory;

import com.robustel.dispatching.domain.elevator.ElevatorId;
import com.robustel.dispatching.domain.elevator.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * @author YangXuehong
 * @date 2022/4/18
 */
class RequestHistoryTest {

    @Test
    void Given_Normal_When_Of_Then_Success() {
        Request request = mock(Request.class);
        RequestHistory history = RequestHistory.of(RequestHistoryId.of(1l), request, ElevatorId.of("1"));
        assertEquals(ElevatorId.of("1"), history.getElevatorId());
        assertEquals(RequestHistoryId.of(1l), history.id());
        assertEquals(request, history.getRequest());
        assertEquals(Instant.now().truncatedTo(ChronoUnit.MINUTES), history.getFinishedOn().truncatedTo(ChronoUnit.MINUTES));
    }

    @Test
    void Given_NullRequest_When_Of_Then_Fail() {
        Assertions.assertThrows(NullPointerException.class,
                () -> RequestHistory.of(RequestHistoryId.of(1l), null, ElevatorId.of("1")));
    }

    @Test
    void Given_NullElevatorId_When_Of_Then_Fail() {
        Request request = mock(Request.class);
        Assertions.assertThrows(NullPointerException.class,
                () -> RequestHistory.of(RequestHistoryId.of(1l), request, null));
    }
}