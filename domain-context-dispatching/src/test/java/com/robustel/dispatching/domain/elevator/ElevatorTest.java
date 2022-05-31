package com.robustel.dispatching.domain.elevator;

import com.robustel.dispatching.domain.InitServiceLocator;
import com.robustel.dispatching.domain.takingrequesthistory.TakingRequestHistory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ElevatorTest {

    private Elevator elevator;

    @BeforeAll
    static void initAll() {
        InitServiceLocator.init();
    }

    @BeforeEach
    void init() {
        elevator = Elevator.create("foobar2000", 10, -2, "foo", "bar");
    }

    @Test
    void Given_Null_When_Create_Then_ThrowsException() {
        assertThrows(NullPointerException.class,
                () -> Elevator.create(null, 10, -1, "foo", "bar"));
        assertThrows(NullPointerException.class,
                () -> Elevator.create("foobar2000", 10, -1, null, "bar"));
        assertThrows(NullPointerException.class,
                () -> Elevator.create("foobar2000", 10, -1, "foo", null));
        assertThrows(IllegalArgumentException.class,
                () -> Elevator.create("foobar2000", -1, 10, "foo", "bar"));
    }

    @Test
    void Given_Normal_When_Create_When_Expected() {
        assertNotNull(elevator.id());
        assertEquals("foobar2000", elevator.getName());
        assertEquals(Floor.of(10), elevator.getHighest());
        assertEquals(Floor.of(-2), elevator.getLowest());
        assertNull(elevator.getCurrentFloor());
        assertEquals(ElevatorState.NONE, elevator.getState());
        assertTrue(elevator.getNotifiedPassengers().isEmpty());
        assertTrue(elevator.getPassengers().isEmpty());
        assertTrue(elevator.getTakingRequests().isEmpty());
    }

    @Test
    void Given_Floor_When_Arrive_Then_CurrentFloorWasSet() {
        elevator.arrive(Floor.of(2));
        assertEquals(Floor.of(2), elevator.getCurrentFloor());
    }

    @Test
    void Given_Null_When_Take_Then_ThrowsException() {
        assertThrows(NullPointerException.class, () -> elevator.take(null, null, null));
        assertThrows(NullPointerException.class, () -> elevator.take(Passenger.of(1L), null, null));
        assertThrows(NullPointerException.class, () -> elevator.take(Passenger.of(1L), Floor.of(1), null));
    }

    @Test
    void Given_TakingRequestOfPassengerExist_When_Take_Then_ThrowsException() {
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), null,
                ElevatorState.NONE, Map.of(1L, mock(TakingRequest.class)), Set.of(Passenger.of(1L)), Set.of());
        assertThrows(TakingRequestAlreadyExistException.class, () -> elevator.take(Passenger.of(1L), Floor.of(1), Floor.of(5)));
    }

    @Test
    void Given_TakingRequestOfPassengerNotExist_When_Take_Then_Expected() {
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), null,
                ElevatorState.NONE, new HashMap<>(), Set.of(Passenger.of(1L)), Set.of());
        elevator.take(Passenger.of(1L), Floor.of(1), Floor.of(5));
        assertNotNull(elevator.getTakingRequests().get(1L));
    }

    @Test
    void Given_UnbindingPassenger_When_Bind_Then_Expected() {
        elevator.bind(Passenger.of(1L));
        assertTrue(elevator.getPassengers().contains(Passenger.of(1L)));
    }

    @Test
    void Given_BindingPassenger_When_Bind_Then_Ignore() {
        elevator.bind(Passenger.of(1L));
        elevator.bind(Passenger.of(1L));
        assertTrue(elevator.getPassengers().contains(Passenger.of(1L)));
    }

    @Test
    void Given_UnbindingPassenger_When_Unbind_Then_Expected() {
        elevator.unbind(Passenger.of(1L));
        assertFalse(elevator.getPassengers().contains(Passenger.of(1L)));
    }

    @Test
    void Given_BindingPassenger_When_Unbind_Then_Ignore() {
        elevator.bind(Passenger.of(1L));
        assertTrue(elevator.isBinding(Passenger.of(1L)));
        elevator.unbind(Passenger.of(1L));
        assertFalse(elevator.getPassengers().contains(Passenger.of(1L)));
    }

    @Test
    void Given_NotOutState_When_TellIn_Then_Ignore() {
        Elevator foobar2000 = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(5),
                ElevatorState.NONE, new HashMap<>(), new HashSet<>(), new HashSet<>());
        foobar2000.tellIn();
        assertEquals(ElevatorState.NONE, foobar2000.getState());
    }

    @Test
    void Given_OutStateWithEmptyRequests_When_TellIn_Then_StateWasSetNone() {
        Elevator foobar2000 = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(5),
                ElevatorState.WAITING_OUT, new HashMap<>(), new HashSet<>(), new HashSet<>());
        foobar2000.tellIn();
        assertEquals(ElevatorState.NONE, elevator.getState());
    }

    @Test
    void Given_OutStateWithNotEmptyRequests_When_TellIn_Then_StateWasSetIn() {
        Map<Long, TakingRequest> takingRequestMap = new HashMap<>();
        takingRequestMap.put(1L, new TakingRequest(1L, Passenger.of(1L), Floor.of(-1), Floor.of(5), Instant.now(), null, null, null));
        Elevator foobar2000 = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(-1),
                ElevatorState.WAITING_OUT, takingRequestMap, new HashSet<>(), new HashSet<>());
        foobar2000.tellIn();
        assertEquals(ElevatorState.WAITING_IN, foobar2000.getState());
    }

    @Test
    void Given_() {
        Map<Long, TakingRequest> takingRequestMap = new HashMap<>();
        takingRequestMap.put(1L, new TakingRequest(1L, Passenger.of(1L), Floor.of(3), Floor.of(5), Instant.now(), null, null, null));
        takingRequestMap.put(2L, new TakingRequest(2L, Passenger.of(2L), Floor.of(2), Floor.of(3), Instant.now(), Instant.now(), null, null));
        Elevator foobar2000 = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(3),
                ElevatorState.NONE, takingRequestMap, new HashSet<>(), new HashSet<>());
        foobar2000.passengerOutIn();
        assertEquals(ElevatorState.WAITING_OUT, foobar2000.getState());
    }

    void Given_2() {
        Map<Long, TakingRequest> takingRequestMap = new HashMap<>();
        takingRequestMap.put(1L, new TakingRequest(1L, Passenger.of(1L), Floor.of(3), Floor.of(5), Instant.now(), null, null, null));
        takingRequestMap.put(2L, new TakingRequest(2L, Passenger.of(2L), Floor.of(2), Floor.of(3), Instant.now(), Instant.now(), null, null));
        Elevator foobar2000 = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(3),
                ElevatorState.NONE, takingRequestMap, new HashSet<>(), new HashSet<>());
        foobar2000.passengerOutIn();
        assertEquals(ElevatorState.WAITING_OUT, foobar2000.getState());
    }

    @Test
    void Given_NoneState_When_CancelTakingRequest_Then_Expected() {
        Map<Long, TakingRequest> takingRequestMap = new HashMap<>();
        takingRequestMap.put(1L, new TakingRequest(1L, Passenger.of(1L), Floor.of(3), Floor.of(5), Instant.now(), null, null, null));
        takingRequestMap.put(2L, new TakingRequest(2L, Passenger.of(2L), Floor.of(2), Floor.of(3), Instant.now(), Instant.now(), null, null));
        Elevator foobar2000 = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(1),
                ElevatorState.NONE, takingRequestMap, Set.of(Passenger.of(1L), Passenger.of(2L)), new HashSet<>());
        assertTrue(foobar2000.getTakingRequests().containsKey(1L));
        foobar2000.cancelTakingRequest(Passenger.of(1L), "Test");
        assertFalse(foobar2000.getTakingRequests().containsKey(1L));
    }

    @Test
    void Given_WaitingOutState_When_CancelTakingRequest_Then_Expected() {
        Map<Long, TakingRequest> takingRequestMap = new HashMap<>();
        takingRequestMap.put(1L, new TakingRequest(1L, Passenger.of(1L), Floor.of(3), Floor.of(5), Instant.now(), null, null, null));
        takingRequestMap.put(2L, new TakingRequest(2L, Passenger.of(2L), Floor.of(2), Floor.of(3), Instant.now(), Instant.now(), null, null));
        Set<Passenger> waitingOut = new HashSet<>();
        waitingOut.add(Passenger.of(2L));
        Elevator foobar2000 = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(3),
                ElevatorState.WAITING_OUT, takingRequestMap, Set.of(Passenger.of(1L), Passenger.of(2L)), waitingOut);
        assertTrue(foobar2000.getTakingRequests().containsKey(2L));
        TakingRequestHistory history = foobar2000.cancelTakingRequest(Passenger.of(2L), "Test");
        assertFalse(foobar2000.getTakingRequests().containsKey(2L));
        assertTrue(elevator.getNotifiedPassengers().isEmpty());
    }

    @Test
    void Given_NoBindings_When_CancelTakingRequest_Then_ThrowsException() {
        assertThrows(PassengerNotAllowedException.class, () -> elevator.cancelTakingRequest(Passenger.of(100L), ""));
    }

    @Test
    void Given_NoTakingRequests_When_CancelTakingRequest_Then_ThrowsException() {
        elevator.bind(Passenger.of(100L));
        assertThrows(TakingRequestNotFoundException.class, () -> elevator.cancelTakingRequest(Passenger.of(100L), ""));
    }

    @Test
    void Given_NoBindings_When_Finish_Then_ThrowsException() {
        assertThrows(PassengerNotAllowedException.class, () -> elevator.finish(Passenger.of(100L)));
    }

    @Test
    void Given_NoneState_When_Finish_Then_ThrowsException() {
        elevator.bind(Passenger.of(1L));
        assertThrows(IllegalStateException.class, () -> elevator.finish(Passenger.of(1L)));
    }

    @Test
    void Given_WaitingOutOrInStateButNotExistRequest_When_Finish_Then_ThrowsException() {
        Map<Long, TakingRequest> takingRequestMap = new HashMap<>();
        takingRequestMap.put(1L, new TakingRequest(1L, Passenger.of(1L), Floor.of(3), Floor.of(5), Instant.now(), null, null, null));
        takingRequestMap.put(2L, new TakingRequest(2L, Passenger.of(2L), Floor.of(2), Floor.of(3), Instant.now(), Instant.now(), null, null));
        Set<Passenger> waitingOut = new HashSet<>();
        waitingOut.add(Passenger.of(2L));
        Elevator foobar2000 = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(3),
                ElevatorState.WAITING_OUT, takingRequestMap, Set.of(Passenger.of(1L), Passenger.of(2L), Passenger.of(3L)), waitingOut);
        assertThrows(TakingRequestNotFoundException.class, () -> foobar2000.finish(Passenger.of(3L)));
    }

    @Test
    void Given_BetweenHighestAndLowest_When_IsValid_Then_ReturnTrue() {
        assertTrue(elevator.isValid(Floor.of(1)));
        assertTrue(elevator.isValid(Floor.of(10)));
        assertTrue(elevator.isValid(Floor.of(-2)));
    }

    @Test
    void Given_OutOfHighestAndLowest_When_IsValid_Then_ReturnFalse() {
        assertFalse(elevator.isValid(Floor.of(-3)));
        assertFalse(elevator.isValid(Floor.of(11)));
    }

    @Test
    void Given_Any_When_Reset_Then_Expected() {
        Map<Long, TakingRequest> takingRequestMap = new HashMap<>();
        takingRequestMap.put(1L, new TakingRequest(1L, Passenger.of(1L), Floor.of(3), Floor.of(5), Instant.now(), null, null, null));
        takingRequestMap.put(2L, new TakingRequest(2L, Passenger.of(2L), Floor.of(2), Floor.of(3), Instant.now(), Instant.now(), null, null));
        Set<Passenger> waitingOut = new HashSet<>();
        waitingOut.add(Passenger.of(2L));
        Elevator foobar2000 = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(3),
                ElevatorState.WAITING_OUT, takingRequestMap, Set.of(Passenger.of(1L), Passenger.of(2L), Passenger.of(3L)), waitingOut);
        foobar2000.reset();
        assertTrue(foobar2000.getNotifiedPassengers().isEmpty());
        assertEquals(ElevatorState.NONE, foobar2000.getState());
    }

}