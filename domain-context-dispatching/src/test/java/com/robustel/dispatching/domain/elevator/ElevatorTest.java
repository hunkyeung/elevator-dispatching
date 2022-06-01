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
        Passenger passenger = Passenger.of(1L);
        Floor floor = Floor.of(1);
        assertThrows(NullPointerException.class, () -> elevator.take(null, null, null));
        assertThrows(NullPointerException.class, () -> elevator.take(passenger, null, null));
        assertThrows(NullPointerException.class, () -> elevator.take(passenger, floor, null));
    }

    @Test
    void Given_TakingRequestOfPassengerExist_When_Take_Then_ThrowsException() {
        Passenger passenger = Passenger.of(1L);
        Floor first = Floor.of(1);
        Floor fifth = Floor.of(5);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), null,
                ElevatorState.NONE, Map.of(1L, mock(TakingRequest.class)), Set.of(passenger), Set.of());
        assertThrows(TakingRequestAlreadyExistException.class, () -> elevator.take(passenger, first, fifth));
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

    /**
     * 没有符合乘客等待进梯
     */
    @Test
    void Given_NoPassengerWaitingIn_When_TellIn_Then_Expected() {
        Elevator foobar2000 = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(5),
                ElevatorState.NONE, new HashMap<>(), new HashSet<>(), new HashSet<>());
        foobar2000.tellIn();
        assertEquals(ElevatorState.NONE, foobar2000.getState());
    }

    /**
     * 有符合的乘客等待进梯
     */
    @Test
    void Given_PassengerWaitingIn_When_TellIn_Then_ElevatorInWaitingInState() {
        Elevator foobar2000 = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(-1),
                ElevatorState.NONE, Map.of(1L, TakingRequest.create(Passenger.of(1L), Floor.of(-1), Floor.of(3))), new HashSet<>(), new HashSet<>());
        foobar2000.tellIn();
        assertEquals(ElevatorState.WAITING_IN, foobar2000.getState());
    }

    @Test
    void Given_PassengerWaitingOut_When_PassengerOutIn_Then_Expected() {
        Map<Long, TakingRequest> takingRequestMap = new HashMap<>();
        takingRequestMap.put(1L, new TakingRequest(1L, Passenger.of(1L), Floor.of(3), Floor.of(5), Instant.now(), null, null, null));
        takingRequestMap.put(2L, new TakingRequest(2L, Passenger.of(2L), Floor.of(2), Floor.of(3), Instant.now(), Instant.now(), null, null));
        Elevator foobar2000 = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(3),
                ElevatorState.NONE, takingRequestMap, new HashSet<>(), new HashSet<>());
        foobar2000.passengerOutIn();
        assertEquals(ElevatorState.WAITING_OUT, foobar2000.getState());
    }

    @Test
    void Given_NoPassengerWaitingOutAndWaitingIn_When_PassengerOutIn_Then_Expected() {
        Map<Long, TakingRequest> takingRequestMap = new HashMap<>();
        takingRequestMap.put(1L, new TakingRequest(1L, Passenger.of(1L), Floor.of(3), Floor.of(5), Instant.now(), null, null, null));
        takingRequestMap.put(2L, new TakingRequest(2L, Passenger.of(2L), Floor.of(2), Floor.of(4), Instant.now(), Instant.now(), null, null));
        takingRequestMap.put(3L, new TakingRequest(3L, Passenger.of(3L), Floor.of(3), Floor.of(10), Instant.now(), null, null, null));
        Elevator foobar2000 = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(9),
                ElevatorState.NONE, takingRequestMap, new HashSet<>(), new HashSet<>());
        foobar2000.passengerOutIn();
        assertEquals(ElevatorState.NONE, foobar2000.getState());
    }

    @Test
    void Given_NoPassengerWaitingOutAndButWaitingIn_When_PassengerOutIn_Then_Expected() {
        Map<Long, TakingRequest> takingRequestMap = new HashMap<>();
        takingRequestMap.put(1L, new TakingRequest(1L, Passenger.of(1L), Floor.of(3), Floor.of(5), Instant.now(), null, null, null));
        takingRequestMap.put(2L, new TakingRequest(2L, Passenger.of(2L), Floor.of(2), Floor.of(4), Instant.now(), Instant.now(), null, null));
        takingRequestMap.put(3L, new TakingRequest(3L, Passenger.of(3L), Floor.of(3), Floor.of(10), Instant.now(), null, null, null));
        Elevator foobar2000 = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(3),
                ElevatorState.NONE, takingRequestMap, new HashSet<>(), new HashSet<>());
        foobar2000.passengerOutIn();
        assertEquals(ElevatorState.WAITING_IN, foobar2000.getState());
    }


    @Test
    void Given_NoBindings_When_CancelTakingRequest_Then_ThrowsException() {
        Passenger _100 = Passenger.of(100L);
        assertThrows(PassengerNotAllowedException.class, () -> elevator.cancelTakingRequest(_100, ""));
    }

    @Test
    void Given_NoTakingRequests_When_CancelTakingRequest_Then_ThrowsException() {
        Passenger _100 = Passenger.of(100L);
        elevator.bind(_100);
        assertThrows(TakingRequestNotFoundException.class, () -> elevator.cancelTakingRequest(_100, ""));
    }

    @Test
    void Given_NotNotifiedPassenger_When_CancelTakingRequest_Then_Expected() {
        Map<Long, TakingRequest> takingRequestMap = new HashMap<>();
        takingRequestMap.put(1L, new TakingRequest(1L, Passenger.of(1L), Floor.of(3), Floor.of(5), Instant.now(), null, null, null));
        takingRequestMap.put(2L, new TakingRequest(2L, Passenger.of(2L), Floor.of(2), Floor.of(3), Instant.now(), Instant.now(), null, null));
        Elevator foobar2000 = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(1),
                ElevatorState.NONE, takingRequestMap, Set.of(Passenger.of(1L), Passenger.of(2L)), new HashSet<>());
        assertTrue(foobar2000.getTakingRequests().containsKey(1L));
        TakingRequestHistory history = foobar2000.cancelTakingRequest(Passenger.of(1L), "Test");
        assertFalse(foobar2000.getTakingRequests().containsKey(1L));
        assertEquals(1L, history.getTakingRequest().id());
    }

    @Test
    void Given_TwoNotifiedPassengerWaitingIn_When_CancelTakingRequestOfOne_Then_Expected() {
        Map<Long, TakingRequest> takingRequestMap = new HashMap<>();
        takingRequestMap.put(1L, new TakingRequest(1L, Passenger.of(1L), Floor.of(2), Floor.of(5), Instant.now(), null, null, null));
        takingRequestMap.put(2L, new TakingRequest(2L, Passenger.of(2L), Floor.of(2), Floor.of(3), Instant.now(), null, null, null));
        Set<Passenger> notifiedPassengers = new HashSet<>();
        notifiedPassengers.add(Passenger.of(1L));
        notifiedPassengers.add(Passenger.of(2L));
        Elevator foobar2000 = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(2),
                ElevatorState.WAITING_IN, takingRequestMap, Set.of(Passenger.of(1L), Passenger.of(2L)), notifiedPassengers);
        assertTrue(foobar2000.getTakingRequests().containsKey(1L));
        assertTrue(foobar2000.getTakingRequests().containsKey(2L));
        foobar2000.cancelTakingRequest(Passenger.of(1L), "Test");
        assertFalse(foobar2000.getTakingRequests().containsKey(1L));
        assertTrue(foobar2000.getTakingRequests().containsKey(2L));
        assertFalse(foobar2000.getNotifiedPassengers().contains(Passenger.of(1L)));
        assertTrue(foobar2000.getNotifiedPassengers().contains(Passenger.of(2L)));
        assertEquals(ElevatorState.WAITING_IN, foobar2000.getState());
    }

    @Test
    void Given_NotifiedPassengerWaitingIn_When_CancelTakingRequest_Then_Expected() {
        Map<Long, TakingRequest> takingRequestMap = new HashMap<>();
        takingRequestMap.put(1L, new TakingRequest(1L, Passenger.of(1L), Floor.of(2), Floor.of(5), Instant.now(), null, null, null));
        takingRequestMap.put(2L, new TakingRequest(2L, Passenger.of(2L), Floor.of(4), Floor.of(3), Instant.now(), null, null, null));
        Set<Passenger> notifiedPassengers = new HashSet<>();
        notifiedPassengers.add(Passenger.of(1L));
        Elevator foobar2000 = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(2),
                ElevatorState.WAITING_IN, takingRequestMap, Set.of(Passenger.of(1L), Passenger.of(2L)), notifiedPassengers);
        assertTrue(foobar2000.getTakingRequests().containsKey(1L));
        foobar2000.cancelTakingRequest(Passenger.of(1L), "Test");
        assertFalse(foobar2000.getTakingRequests().containsKey(1L));
        assertFalse(foobar2000.getNotifiedPassengers().contains(Passenger.of(1L)));
        assertEquals(ElevatorState.NONE, foobar2000.getState());
    }

    @Test
    void Given_TwoNotifiedPassengerWaitingOut_When_CancelTakingRequestOfOne_Then_Expected() {
        Map<Long, TakingRequest> takingRequestMap = new HashMap<>();
        takingRequestMap.put(1L, new TakingRequest(1L, Passenger.of(1L), Floor.of(1), Floor.of(5), Instant.now(), null, null, null));
        takingRequestMap.put(2L, new TakingRequest(2L, Passenger.of(2L), Floor.of(2), Floor.of(5), Instant.now(), null, null, null));
        Set<Passenger> notifiedPassengers = new HashSet<>();
        notifiedPassengers.add(Passenger.of(1L));
        notifiedPassengers.add(Passenger.of(2L));
        Elevator foobar2000 = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(5),
                ElevatorState.WAITING_OUT, takingRequestMap, Set.of(Passenger.of(1L), Passenger.of(2L)), notifiedPassengers);
        assertTrue(foobar2000.getTakingRequests().containsKey(1L));
        assertTrue(foobar2000.getTakingRequests().containsKey(2L));
        foobar2000.cancelTakingRequest(Passenger.of(1L), "Test");
        assertFalse(foobar2000.getTakingRequests().containsKey(1L));
        assertTrue(foobar2000.getTakingRequests().containsKey(2L));
        assertFalse(foobar2000.getNotifiedPassengers().contains(Passenger.of(1L)));
        assertTrue(foobar2000.getNotifiedPassengers().contains(Passenger.of(2L)));
        assertEquals(ElevatorState.WAITING_OUT, foobar2000.getState());
    }

    @Test
    void Given_NotifiedPassengerWaitingOut_When_CancelTakingRequest_Then_Expected() {
        Map<Long, TakingRequest> takingRequestMap = new HashMap<>();
        takingRequestMap.put(1L, new TakingRequest(1L, Passenger.of(1L), Floor.of(2), Floor.of(5), Instant.now(), null, null, null));
        takingRequestMap.put(2L, new TakingRequest(2L, Passenger.of(2L), Floor.of(4), Floor.of(3), Instant.now(), null, null, null));
        Set<Passenger> notifiedPassengers = new HashSet<>();
        notifiedPassengers.add(Passenger.of(1L));
        Elevator foobar2000 = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(5),
                ElevatorState.WAITING_OUT, takingRequestMap, Set.of(Passenger.of(1L), Passenger.of(2L)), notifiedPassengers);
        assertTrue(foobar2000.getTakingRequests().containsKey(1L));
        foobar2000.cancelTakingRequest(Passenger.of(1L), "Test");
        assertFalse(foobar2000.getTakingRequests().containsKey(1L));
        assertFalse(foobar2000.getNotifiedPassengers().contains(Passenger.of(1L)));
        assertEquals(ElevatorState.NONE, foobar2000.getState());
    }


    @Test
    void Given_NoBindings_When_Finish_Then_ThrowsException() {
        Passenger _100 = Passenger.of(100L);
        assertThrows(PassengerNotAllowedException.class, () -> elevator.finish(_100));
    }

    @Test
    void Given_NoneState_When_Finish_Then_ThrowsException() {
        Passenger one = Passenger.of(1L);
        elevator.bind(one);
        assertThrows(IllegalStateException.class, () -> elevator.finish(one));
    }

    @Test
    void Given_NoPassengerNotified_When_Finish_Then_ThrowsException() {
        Passenger one = Passenger.of(1L);
        Passenger two = Passenger.of(2L);
        Map<Long, TakingRequest> takingRequestMap = new HashMap<>();
        takingRequestMap.put(1L, new TakingRequest(1L, one, Floor.of(2), Floor.of(5), Instant.now(), null, null, null));
        takingRequestMap.put(2L, new TakingRequest(2L, two, Floor.of(4), Floor.of(3), Instant.now(), null, null, null));
        Set<Passenger> notifiedPassengers = new HashSet<>();
        notifiedPassengers.add(one);
        Elevator foobar2000 = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(5),
                ElevatorState.WAITING_OUT, takingRequestMap, Set.of(one, two), notifiedPassengers);
        assertThrows(IllegalStateException.class, () -> foobar2000.finish(two));
    }

    @Test
    void Given_PassengerNotified_When_Finish_Then_Expected() {
        Map<Long, TakingRequest> takingRequestMap = new HashMap<>();
        takingRequestMap.put(1L, new TakingRequest(1L, Passenger.of(1L), Floor.of(2), Floor.of(5), Instant.now(), Instant.now(), null, null));
        takingRequestMap.put(2L, new TakingRequest(2L, Passenger.of(2L), Floor.of(4), Floor.of(3), Instant.now(), null, null, null));
        Set<Passenger> notifiedPassengers = new HashSet<>();
        notifiedPassengers.add(Passenger.of(1L));
        Elevator foobar2000 = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(5),
                ElevatorState.WAITING_OUT, takingRequestMap, Set.of(Passenger.of(1L), Passenger.of(2L)), notifiedPassengers);
        foobar2000.finish(Passenger.of(1L));
        assertEquals(ElevatorState.NONE, foobar2000.getState());
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