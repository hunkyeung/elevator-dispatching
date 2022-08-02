package com.robustel.dispatching.domain.elevator;

import com.robustel.dispatching.domain.InitServiceLocator;
import com.robustel.dispatching.domain.requesthistory.RequestHistory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

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
        elevator = Elevator.create("foobar2000", 10, -2);
    }

    @Test
    void Given_Normal_When_Create_When_Expected() {
        assertNotNull(elevator.id());
        assertEquals("foobar2000", elevator.getName());
        assertEquals(Floor.of(10), elevator.getHighest());
        assertEquals(Floor.of(-2), elevator.getLowest());
        assertNull(elevator.getCurrentFloor());
        assertEquals(ElevatorState.NONE, elevator.getState());
        assertTrue(elevator.getToBeNotified().isEmpty());
        assertNull(elevator.getNotified());
        assertTrue(elevator.getOnPassage().isEmpty());
        assertTrue(elevator.getBinding().isEmpty());
        assertTrue(elevator.getRequests().isEmpty());
        assertTrue(elevator.getTransferPassengers().isEmpty());
    }

    @Test
    void Given_HighestLessThanLowest_When_Create_When_Exception() {
        assertThrows(IllegalArgumentException.class, () -> Elevator.create("foobar2000", -2, 10));
    }


    @Test
    void Given_FloorAndDirection_When_Open_Then_CurrentFloorAndDirectionWereSet() {
        elevator.arrive(Floor.of(2), Direction.UP);
        assertNull(elevator.getCurrentFloor());
        assertNull(elevator.getNextDirection());
    }


    @Test
    void Given_RequestOfPassengerExist_When_Take_Then_ThrowsException() {
        Passenger passenger = Passenger.of("1");
        Floor first = Floor.of(1);
        Floor fifth = Floor.of(5);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), null, Direction.STOP,
                ElevatorState.NONE, new HashMap<>(), new ArrayList<>(), Set.of(passenger), null,
                new ArrayList<>(), new ArrayList<>(), new HashSet<>());
        elevator.take(passenger, first, fifth);
        assertTrue(elevator.getPressedFloor().contains(first));
        assertThrows(Elevator.RequestAlreadyExistException.class, () -> elevator.take(passenger, first, fifth));
    }


    @Test
    void Given_RequestOfPassengerNotExist_When_Take_Then_Expected() {
        Passenger passenger = Passenger.of("1");
        Map<String, Request> requests = new HashMap<>();
        Floor first = Floor.of(1);
        Floor fifth = Floor.of(5);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), null, Direction.STOP,
                ElevatorState.NONE, requests, new ArrayList<>(), Set.of(passenger), null, new ArrayList<>(), new ArrayList<>(), new HashSet<>());
        elevator.take(Passenger.of("1"), first, fifth);
        assertTrue(elevator.getPressedFloor().contains(first));
        assertNotNull(elevator.getRequests().get("1"));
    }

    @Test
    void Given_RequestOfPassengerNotExist_When_Take_Then_Expected2() {
        Passenger passenger = Passenger.of("1");
        Map<String, Request> requests = new HashMap<>();
        Floor first = Floor.of(1);
        Floor fifth = Floor.of(5);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), null, Direction.STOP,
                ElevatorState.NONE, requests, new ArrayList<>(), Set.of(passenger), null, new ArrayList<>(), new ArrayList<>(), new HashSet<>());
        elevator.take(Passenger.of("1"), first, fifth);
        elevator.take(Passenger.of("2"), first, Floor.of(10));
        assertTrue(elevator.getPressedFloor().contains(first));
        assertEquals(1, elevator.getPressedFloor().size());
        assertNotNull(elevator.getRequests().get("1"));
        assertNotNull(elevator.getRequests().get("2"));
    }

    @Test
    void Given_UnbindingPassenger_When_Bind_Then_Expected() {
        elevator.bind(Passenger.of("1"));
        assertTrue(elevator.getBinding().contains(Passenger.of("1")));
    }

    @Test
    void Given_BindingPassenger_When_Bind_Then_Ignore() {
        elevator.bind(Passenger.of("1"));
        elevator.bind(Passenger.of("1"));
        assertTrue(elevator.getBinding().contains(Passenger.of("1")));
    }

    @Test
    void Given_UnbindingPassenger_When_Unbind_Then_Expected() {
        elevator.unbind(Passenger.of("1"));
        assertFalse(elevator.getBinding().contains(Passenger.of("1")));
    }

    @Test
    void Given_BindingPassenger_When_Unbind_Then_Ignore() {
        elevator.bind(Passenger.of("1"));
        assertTrue(elevator.isBinding(Passenger.of("1")));
        elevator.unbind(Passenger.of("1"));
        assertFalse(elevator.getBinding().contains(Passenger.of("1")));
    }

    @Test
    void Given_In_When_IsMatched_Then_ReturnFalse() {
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(10), Floor.of(-1), null, Direction.STOP,
                ElevatorState.NONE, Map.of("1", mock(Request.class)), new ArrayList<>(), Set.of(Passenger.of("1")),
                null, new ArrayList<>(), new ArrayList<>(), new HashSet<>());
        assertTrue(elevator.isMatched(Floor.of(-1), Floor.of(10)));
    }

    @Test
    void Given_Out_When_IsMatched_Then_ReturnTrue() {
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(10), Floor.of(-1), null, Direction.STOP,
                ElevatorState.NONE, new HashMap<>(), new ArrayList<>(), Set.of(Passenger.of("1")), null, new ArrayList<>(), new ArrayList<>(), new HashSet<>());
        assertFalse(elevator.isMatched(Floor.of(-1), Floor.of(20)));
        assertFalse(elevator.isMatched(Floor.of(-6), Floor.of(7)));
    }

    @Test
    void Given_When_ReleaseDoor_Then_Expected() {
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), null, Direction.STOP,
                ElevatorState.NONE, new HashMap<>(), new ArrayList<>(), Set.of(Passenger.of("1")), Passenger.of("1"),
                new ArrayList<>(), new ArrayList<>(), new HashSet<>());
        elevator.release();
        assertNull(elevator.getNotified());
        assertEquals(ElevatorState.NONE, elevator.getState());
    }

    @Test
    void Given_AnyStateMode_When_CancelRequestWithNotExistRequest_Then_ThrowsRequestNotFoundException() {
        Passenger firstPassenger = Passenger.of("1");
        Map<String, Request> requests = new HashMap<>();
        Floor firstFloor = Floor.of(1);
        Floor fifthFloor = Floor.of(5);
        Request firstRequest = new Request(1L, firstPassenger, firstFloor, fifthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("1", firstRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        List<Passenger> onPassage = new ArrayList<>();
        onPassage.add(firstPassenger);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP,
                ElevatorState.NONE, requests, toBeNotified, Set.of(firstPassenger), null, onPassage, new ArrayList<>(), new HashSet<>());
        Passenger of = Passenger.of("100");
        Assertions.assertThrows(Elevator.RequestNotFoundException.class, () -> elevator.cancelRequest(of, ""));
    }

    @Test
    void Given_NoneStateMode_When_Finish_Then_ThrowsIllegalStateException() {
        Passenger firstPassenger = Passenger.of("1");
        Map<String, Request> requests = new HashMap<>();
        Floor firstFloor = Floor.of(1);
        Floor fifthFloor = Floor.of(5);
        Request firstRequest = new Request(1L, firstPassenger, firstFloor, fifthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("1", firstRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        List<Passenger> onPassage = new ArrayList<>();
        onPassage.add(firstPassenger);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP,
                ElevatorState.NONE, requests, toBeNotified, Set.of(firstPassenger), null, onPassage, new ArrayList<>(), new HashSet<>());
        Passenger of = Passenger.of("100");
        Assertions.assertThrows(IllegalStateException.class, () -> elevator.finish(of));
    }

    @Test
    void Given_NoneStateOnPassage_When_CancelRequest_Then_Expected() {
        Passenger firstPassenger = Passenger.of("1");
        Map<String, Request> requests = new HashMap<>();
        Floor firstFloor = Floor.of(1);
        Floor fifthFloor = Floor.of(5);
        Request firstRequest = new Request(1L, firstPassenger, firstFloor, fifthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("1", firstRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        List<Passenger> onPassage = new ArrayList<>();
        onPassage.add(firstPassenger);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP,
                ElevatorState.NONE, requests, toBeNotified, Set.of(firstPassenger), null, onPassage, new ArrayList<>(), new HashSet<>());
        RequestHistory requestHistory = elevator.cancelRequest(firstPassenger, "for test");
        assertNotNull(requestHistory);
        assertEquals("for test", firstRequest.getStatus());
        assertFalse(onPassage.contains(firstPassenger));
        assertFalse(elevator.getRequests().containsValue(firstRequest));
    }

    @Test
    void Given_NoneStateWithRequest_When_CancelRequest_Then_Expected() {
        Passenger firstPassenger = Passenger.of("1");
        Map<String, Request> requests = new HashMap<>();
        Floor firstFloor = Floor.of(1);
        Floor fifthFloor = Floor.of(5);
        Request firstRequest = new Request(1L, firstPassenger, firstFloor, fifthFloor, Instant.now(), null, null, null);
        requests.put("1", firstRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP,
                ElevatorState.NONE, requests, toBeNotified, Set.of(firstPassenger), null, new ArrayList<>(), new ArrayList<>(), new HashSet<>());
        RequestHistory requestHistory = elevator.cancelRequest(firstPassenger, "for test");
        assertNotNull(requestHistory);
        assertEquals("for test", firstRequest.getStatus());
        assertFalse(elevator.getRequests().containsValue(firstRequest));
    }

    @Test
    void Given_WaitingOutState_When_CancelRequestWithNotified_Then_Expected() {
        Passenger firstPassenger = Passenger.of("1");
        Passenger secondPassenger = Passenger.of("2");
        Passenger thirdPassenger = Passenger.of("3");
        Map<String, Request> requests = new HashMap<>();
        Floor firstFloor = Floor.of(1);
        Floor fifthFloor = Floor.of(5);
        Request firstRequest = new Request(1L, firstPassenger, firstFloor, fifthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("1", firstRequest);
        Request secondRequest = new Request(2L, secondPassenger, firstFloor, fifthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("2", secondRequest);
        Request thirdRequest = new Request(3L, thirdPassenger, firstFloor, fifthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("3", thirdRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        toBeNotified.add(secondPassenger);
        toBeNotified.add(thirdPassenger);
        List<Passenger> onPassage = new ArrayList<>();
        onPassage.add(thirdPassenger);
        onPassage.add(secondPassenger);
        onPassage.add(firstPassenger);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP,
                ElevatorState.WAITING_OUT, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), firstPassenger, onPassage, new ArrayList<>(), new HashSet<>());
        elevator.cancelRequest(firstPassenger, "for test");
        assertEquals(ElevatorState.WAITING_OUT, elevator.getState());
        assertEquals(2, onPassage.size());
        assertEquals(secondPassenger, elevator.getNotified());
        assertEquals(1, elevator.getToBeNotified().size());
    }

    @Test
    void Given_WaitingOutState_When_CancelRequestWithoutNotifiedButInToBeNotified_Then_Expected() {
        Passenger firstPassenger = Passenger.of("1");
        Passenger secondPassenger = Passenger.of("2");
        Passenger thirdPassenger = Passenger.of("3");
        Map<String, Request> requests = new HashMap<>();
        Floor firstFloor = Floor.of(1);
        Floor fifthFloor = Floor.of(5);
        Request firstRequest = new Request(1L, firstPassenger, firstFloor, fifthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("1", firstRequest);
        Request secondRequest = new Request(2L, secondPassenger, firstFloor, fifthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("2", secondRequest);
        Request thirdRequest = new Request(3L, thirdPassenger, firstFloor, fifthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("3", thirdRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        toBeNotified.add(secondPassenger);
        toBeNotified.add(thirdPassenger);
        List<Passenger> onPassage = new ArrayList<>();
        onPassage.add(thirdPassenger);
        onPassage.add(secondPassenger);
        onPassage.add(firstPassenger);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP,
                ElevatorState.WAITING_OUT, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), firstPassenger, onPassage, new ArrayList<>(), new HashSet<>());
        elevator.cancelRequest(secondPassenger, "for test");
        assertEquals(ElevatorState.WAITING_OUT, elevator.getState());
        assertEquals(2, onPassage.size());
        assertEquals(firstPassenger, elevator.getNotified());
        assertEquals(1, elevator.getToBeNotified().size());
    }

    @Test
    void Given_WaitingOutState_When_CancelRequestWithoutNotifiedAndNotInToBeNotified_Then_Expected() {
        Passenger firstPassenger = Passenger.of("1");
        Passenger secondPassenger = Passenger.of("2");
        Passenger thirdPassenger = Passenger.of("3");
        Map<String, Request> requests = new HashMap<>();
        Floor firstFloor = Floor.of(1);
        Floor fifthFloor = Floor.of(5);
        Request firstRequest = new Request(1L, firstPassenger, firstFloor, fifthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("1", firstRequest);
        Request secondRequest = new Request(2L, secondPassenger, firstFloor, fifthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("2", secondRequest);
        Request thirdRequest = new Request(3L, thirdPassenger, firstFloor, fifthFloor, Instant.now(), null, null, null);
        requests.put("3", thirdRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        toBeNotified.add(secondPassenger);
        List<Passenger> onPassage = new ArrayList<>();
        onPassage.add(secondPassenger);
        onPassage.add(firstPassenger);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP,
                ElevatorState.WAITING_OUT, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), firstPassenger, onPassage, new ArrayList<>(), new HashSet<>());
        elevator.cancelRequest(thirdPassenger, "for test");
        assertEquals(ElevatorState.WAITING_OUT, elevator.getState());
        assertFalse(elevator.getRequests().containsValue(thirdRequest));
        assertEquals(2, onPassage.size());
        assertEquals(firstPassenger, elevator.getNotified());
        assertEquals(1, elevator.getToBeNotified().size());
    }

    @Test
    void Given_WaitingOutState_When_FinishWithoutNotified_Then_ThrowsIllegalStateException() {
        Passenger firstPassenger = Passenger.of("1");
        Passenger secondPassenger = Passenger.of("2");
        Passenger thirdPassenger = Passenger.of("3");
        Map<String, Request> requests = new HashMap<>();
        Floor firstFloor = Floor.of(1);
        Floor fifthFloor = Floor.of(5);
        Request firstRequest = new Request(1L, firstPassenger, firstFloor, fifthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("1", firstRequest);
        Request secondRequest = new Request(2L, secondPassenger, firstFloor, fifthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("2", secondRequest);
        Request thirdRequest = new Request(3L, thirdPassenger, firstFloor, fifthFloor, Instant.now(), null, null, null);
        requests.put("3", thirdRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        toBeNotified.add(secondPassenger);
        List<Passenger> onPassage = new ArrayList<>();
        onPassage.add(secondPassenger);
        onPassage.add(firstPassenger);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP,
                ElevatorState.WAITING_OUT, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), firstPassenger, onPassage, new ArrayList<>(), new HashSet<>());
        Assertions.assertThrows(IllegalStateException.class, () -> elevator.finish(thirdPassenger));

    }

    @Test
    void Given_WaitingOutState_When_FinishWithNotified_Then_Expected() {
        Passenger firstPassenger = Passenger.of("1");
        Passenger secondPassenger = Passenger.of("2");
        Passenger thirdPassenger = Passenger.of("3");
        Map<String, Request> requests = new HashMap<>();
        Floor firstFloor = Floor.of(1);
        Floor fifthFloor = Floor.of(5);
        Request firstRequest = new Request(1L, firstPassenger, firstFloor, fifthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("1", firstRequest);
        Request secondRequest = new Request(2L, secondPassenger, firstFloor, fifthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("2", secondRequest);
        Request thirdRequest = new Request(3L, thirdPassenger, firstFloor, fifthFloor, Instant.now(), null, null, null);
        requests.put("3", thirdRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        toBeNotified.add(secondPassenger);
        List<Passenger> onPassage = new ArrayList<>();
        onPassage.add(secondPassenger);
        onPassage.add(firstPassenger);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP,
                ElevatorState.WAITING_OUT, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), firstPassenger, onPassage, new ArrayList<>(), new HashSet<>());
        Optional<RequestHistory> firstRequestHistory = elevator.finish(firstPassenger);
        assertFalse(firstRequestHistory.isEmpty());
        assertEquals(ElevatorState.WAITING_OUT, elevator.getState());
        assertEquals(secondPassenger, elevator.getNotified());
        assertFalse(onPassage.contains(firstPassenger));
        assertEquals(0, toBeNotified.size());

    }

    //Waiting in State Mode

    @Test
    void Given_WaitingInState_When_CancelRequestWithNotified_Then_Expected() {
        Passenger firstPassenger = Passenger.of("1");
        Passenger secondPassenger = Passenger.of("2");
        Passenger thirdPassenger = Passenger.of("3");
        Map<String, Request> requests = new HashMap<>();
        Floor firstFloor = Floor.of(1);
        Floor fifthFloor = Floor.of(5);
        Request firstRequest = new Request(1L, firstPassenger, firstFloor, fifthFloor, Instant.now(), null, null, null);
        requests.put("1", firstRequest);
        Request secondRequest = new Request(2L, secondPassenger, firstFloor, fifthFloor, Instant.now(), null, null, null);
        requests.put("2", secondRequest);
        Request thirdRequest = new Request(3L, thirdPassenger, firstFloor, fifthFloor, Instant.now(), null, null, null);
        requests.put("3", thirdRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        toBeNotified.add(secondPassenger);
        toBeNotified.add(thirdPassenger);
        List<Passenger> onPassage = new ArrayList<>();
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), firstFloor, Direction.UP,
                ElevatorState.WAITING_IN, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), firstPassenger, onPassage, new ArrayList<>(), new HashSet<>());
        elevator.cancelRequest(firstPassenger, "for test");
        assertEquals(ElevatorState.WAITING_IN, elevator.getState());
        assertTrue(onPassage.isEmpty());
        assertEquals(secondPassenger, elevator.getNotified());
        assertEquals(1, elevator.getToBeNotified().size());
    }

    @Test
    void Given_WaitingInState_When_CancelRequestWithoutNotifiedButInToBeNotified_Then_Expected() {
        Passenger firstPassenger = Passenger.of("1");
        Passenger secondPassenger = Passenger.of("2");
        Passenger thirdPassenger = Passenger.of("3");
        Map<String, Request> requests = new HashMap<>();
        Floor firstFloor = Floor.of(1);
        Floor fifthFloor = Floor.of(5);
        Request firstRequest = new Request(1L, firstPassenger, firstFloor, fifthFloor, Instant.now(), null, null, null);
        requests.put("1", firstRequest);
        Request secondRequest = new Request(2L, secondPassenger, firstFloor, fifthFloor, Instant.now(), null, null, null);
        requests.put("2", secondRequest);
        Request thirdRequest = new Request(3L, thirdPassenger, firstFloor, fifthFloor, Instant.now(), null, null, null);
        requests.put("3", thirdRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        toBeNotified.add(secondPassenger);
        toBeNotified.add(thirdPassenger);
        List<Passenger> onPassage = new ArrayList<>();
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP,
                ElevatorState.WAITING_IN, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), firstPassenger, onPassage, new ArrayList<>(), new HashSet<>());
        elevator.cancelRequest(secondPassenger, "for test");
        assertEquals(ElevatorState.WAITING_IN, elevator.getState());
        assertEquals(firstPassenger, elevator.getNotified());
        assertEquals(1, elevator.getToBeNotified().size());
        assertFalse(elevator.getToBeNotified().contains(secondPassenger));
    }

    @Test
    void Given_WaitingInState_When_CancelRequestWithoutNotifiedAndNotInToBeNotified_Then_Expected() {
        Passenger firstPassenger = Passenger.of("1");
        Passenger secondPassenger = Passenger.of("2");
        Passenger thirdPassenger = Passenger.of("3");
        Map<String, Request> requests = new HashMap<>();
        Floor firstFloor = Floor.of(1);
        Floor fifthFloor = Floor.of(5);
        Request firstRequest = new Request(1L, firstPassenger, firstFloor, fifthFloor, Instant.now(), null, null, null);
        requests.put("1", firstRequest);
        Request secondRequest = new Request(2L, secondPassenger, firstFloor, fifthFloor, Instant.now(), null, null, null);
        requests.put("2", secondRequest);
        Request thirdRequest = new Request(3L, thirdPassenger, Floor.of(2), fifthFloor, Instant.now(), null, null, null);
        requests.put("3", thirdRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        toBeNotified.add(secondPassenger);
        List<Passenger> onPassage = new ArrayList<>();
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP,
                ElevatorState.WAITING_IN, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), firstPassenger, onPassage, new ArrayList<>(), new HashSet<>());
        elevator.cancelRequest(thirdPassenger, "for test");
        assertEquals(ElevatorState.WAITING_IN, elevator.getState());
        assertFalse(elevator.getRequests().containsValue(thirdRequest));
        assertEquals(firstPassenger, elevator.getNotified());
        assertEquals(1, elevator.getToBeNotified().size());
    }

    @Test
    void Given_WaitingInState_When_FinishWithoutNotified_Then_ThrowsIllegalStateException() {
        Passenger firstPassenger = Passenger.of("1");
        Passenger secondPassenger = Passenger.of("2");
        Passenger thirdPassenger = Passenger.of("3");
        Map<String, Request> requests = new HashMap<>();
        Floor firstFloor = Floor.of(1);
        Floor fifthFloor = Floor.of(5);
        Request firstRequest = new Request(1L, firstPassenger, firstFloor, fifthFloor, Instant.now(), null, null, null);
        requests.put("1", firstRequest);
        Request secondRequest = new Request(2L, secondPassenger, firstFloor, fifthFloor, Instant.now(), null, null, null);
        requests.put("2", secondRequest);
        Request thirdRequest = new Request(3L, thirdPassenger, firstFloor, fifthFloor, Instant.now(), null, null, null);
        requests.put("3", thirdRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        toBeNotified.add(secondPassenger);
        List<Passenger> onPassage = new ArrayList<>();
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP,
                ElevatorState.WAITING_IN, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), firstPassenger, onPassage, new ArrayList<>(), new HashSet<>());
        Assertions.assertThrows(IllegalStateException.class, () -> elevator.finish(thirdPassenger));

    }

    @Test
    void Given_WaitingInState_When_FinishWithNotified_Then_Expected() {
        Passenger firstPassenger = Passenger.of("1");
        Passenger secondPassenger = Passenger.of("2");
        Passenger thirdPassenger = Passenger.of("3");
        Map<String, Request> requests = new HashMap<>();
        Floor firstFloor = Floor.of(1);
        Floor fifthFloor = Floor.of(5);
        Request firstRequest = new Request(1L, firstPassenger, firstFloor, fifthFloor, Instant.now(), null, null, null);
        requests.put("1", firstRequest);
        Request secondRequest = new Request(2L, secondPassenger, firstFloor, fifthFloor, Instant.now(), null, null, null);
        requests.put("2", secondRequest);
        Request thirdRequest = new Request(3L, thirdPassenger, firstFloor, fifthFloor, Instant.now(), null, null, null);
        requests.put("3", thirdRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        toBeNotified.add(secondPassenger);
        List<Passenger> onPassage = new ArrayList<>();
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP,
                ElevatorState.WAITING_IN, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), firstPassenger, onPassage, new ArrayList<>(), new HashSet<>());
        Optional<RequestHistory> firstRequestHistory = elevator.finish(firstPassenger);
        assertTrue(firstRequestHistory.isEmpty());
        assertEquals(ElevatorState.WAITING_IN, elevator.getState());
        assertEquals(secondPassenger, elevator.getNotified());
        assertTrue(onPassage.contains(firstPassenger));
        assertEquals(0, toBeNotified.size());

    }

}