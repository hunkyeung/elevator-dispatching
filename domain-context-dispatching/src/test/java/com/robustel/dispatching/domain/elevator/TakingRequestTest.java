package com.robustel.dispatching.domain.elevator;

import com.robustel.dispatching.domain.InitServiceLocator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class TakingRequestTest {

    private TakingRequest of;

    @BeforeAll
    static void initALl() {
        InitServiceLocator.init();
    }

    @BeforeEach
    void init() {
        of = TakingRequest.create(Passenger.of(1L), Floor.of(-1), Floor.of(2));
    }

    @Test
    void Given_Null_When_Create_Then_ThrowException() {
        assertThrows(NullPointerException.class,
                () -> TakingRequest.create(null, Floor.of(-1), Floor.of(2)));
        assertThrows(NullPointerException.class,
                () -> TakingRequest.create(Passenger.of(1L), null, Floor.of(2)));
        assertThrows(NullPointerException.class,
                () -> TakingRequest.create(Passenger.of(1L), Floor.of(-1), null));
        assertThrows(IllegalArgumentException.class,
                () -> TakingRequest.create(Passenger.of(1L), Floor.of(-1), Floor.of(-1)));
    }

    @Test
    void Given_Cause_When_Cancel_Then_CauseWasSet() {
        assertNull(of.getStatus());
        of.cancel("Fail");
        assertEquals("Fail", of.getStatus());
    }

    @Test
    void Given_NoneState_When_Finish_Then_ThrowsIllegalStateException() {
        assertThrows(IllegalStateException.class,
                () -> of.finish(ElevatorState.NONE));
    }

    @Test
    void Given_InState_When_Finish_Then_InWasSet() {
        assertNull(of.getIn());
        of.finish(ElevatorState.WAITING_IN);
        assertEquals(Instant.now().truncatedTo(ChronoUnit.SECONDS), of.getIn().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    void Given_OutState_When_Finish_Then_OutWasSet() {
        TakingRequest of = new TakingRequest(1L, Passenger.of(1L), Floor.of(-1), Floor.of(2), Instant.now(), Instant.now(), null, null);
        assertNull(of.getOut());
        of.finish(ElevatorState.WAITING_OUT);
        assertEquals(Instant.now().truncatedTo(ChronoUnit.SECONDS), of.getOut().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    void Given_InStateAndFromFloor_When_Action_Then_Expected() {
        TakingRequest notIn = new TakingRequest(1L, Passenger.of(1L), Floor.of(-1), Floor.of(2), Instant.now(), null, null, null);
        assertTrue(notIn.action(ElevatorState.WAITING_IN, Floor.of(-1)));
        TakingRequest in = new TakingRequest(1L, Passenger.of(1L), Floor.of(-1), Floor.of(2), Instant.now(), Instant.now(), null, null);
        assertFalse(in.action(ElevatorState.WAITING_IN, Floor.of(-1)));
        TakingRequest fromFloorNotMatched = new TakingRequest(1L, Passenger.of(1L), Floor.of(-1), Floor.of(2), Instant.now(), Instant.now(), null, null);
        assertFalse(fromFloorNotMatched.action(ElevatorState.WAITING_IN, Floor.of(-1)));
    }

    @Test
    void Given_OutStateAndToFloor_When_Action_Then_Expected() {
        TakingRequest notOut = new TakingRequest(1L, Passenger.of(1L), Floor.of(-1), Floor.of(2), Instant.now(), Instant.now(), null, null);
        assertTrue(notOut.action(ElevatorState.WAITING_OUT, Floor.of(2)));
        TakingRequest out = new TakingRequest(1L, Passenger.of(1L), Floor.of(-1), Floor.of(2), Instant.now(), Instant.now(), Instant.now(), null);
        assertFalse(out.action(ElevatorState.WAITING_IN, Floor.of(2)));
        TakingRequest toFloorNotMatched = new TakingRequest(1L, Passenger.of(1L), Floor.of(-1), Floor.of(2), Instant.now(), Instant.now(), null, null);
        assertFalse(toFloorNotMatched.action(ElevatorState.WAITING_OUT, Floor.of(3)));
    }

    @Test
    void Given_NoneState_When_Action_Then_ReturnFalse() {
        assertFalse(of.action(ElevatorState.NONE, Floor.of(-1)));
    }

}