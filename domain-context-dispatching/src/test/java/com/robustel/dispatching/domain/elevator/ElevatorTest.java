package com.robustel.dispatching.domain.elevator;

import com.robustel.dispatching.domain.InitServiceLocator;
import com.robustel.dispatching.domain.requesthistory.RequestHistory;
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
        assertTrue(elevator.getToBeNotified().isEmpty());
        assertNull(elevator.getNotified());
        assertTrue(elevator.getOnPassage().isEmpty());
        assertTrue(elevator.getPassengers().isEmpty());
        assertTrue(elevator.getRequests().isEmpty());
        assertTrue(elevator.getTransferStation().isEmpty());
    }

    @Test
    void Given_HighestLessThanLowest_When_Create_When_Exception() {
        assertThrows(IllegalArgumentException.class, () -> Elevator.create("foobar2000", -2, 10, "foo", "bar"));
    }


    @Test
    void Given_FloorAndDirection_When_Open_Then_CurrentFloorAndDirectionWereSet() {
        elevator.open(Floor.of(2), Direction.UP);
        assertEquals(Floor.of(2), elevator.getCurrentFloor());
        assertEquals(Direction.UP, elevator.getDirection());
    }


    @Test
    void Given_RequestOfPassengerExist_When_Take_Then_ThrowsException() {
        Passenger passenger = Passenger.of("1");
        Floor first = Floor.of(1);
        Floor fifth = Floor.of(5);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), null, Direction.STOP,
                ElevatorState.NONE, Map.of("1", mock(Request.class)), new ArrayList<>(), Set.of(passenger), null, new ArrayList<>(), new ArrayList<>());
        assertThrows(RequestAlreadyExistException.class, () -> elevator.take(passenger, first, fifth));
    }


    @Test
    void Given_RequestOfPassengerNotExist_When_Take_Then_Expected() {
        Passenger passenger = Passenger.of("1");
        Map<String, Request> requests = new HashMap<>();
        requests.put("2", mock(Request.class));
        Floor first = Floor.of(1);
        Floor fifth = Floor.of(5);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), null, Direction.STOP,
                ElevatorState.NONE, requests, new ArrayList<>(), Set.of(passenger), null, new ArrayList<>(), new ArrayList<>());
        elevator.take(Passenger.of("1"), first, fifth);
        assertNotNull(elevator.getRequests().get("1"));
    }

    @Test
    void Given_UnbindingPassenger_When_Bind_Then_Expected() {
        elevator.bind(Passenger.of("1"));
        assertTrue(elevator.getPassengers().contains(Passenger.of("1")));
    }

    @Test
    void Given_BindingPassenger_When_Bind_Then_Ignore() {
        elevator.bind(Passenger.of("1"));
        elevator.bind(Passenger.of("1"));
        assertTrue(elevator.getPassengers().contains(Passenger.of("1")));
    }

    @Test
    void Given_UnbindingPassenger_When_Unbind_Then_Expected() {
        elevator.unbind(Passenger.of("1"));
        assertFalse(elevator.getPassengers().contains(Passenger.of("1")));
    }

    @Test
    void Given_BindingPassenger_When_Unbind_Then_Ignore() {
        elevator.bind(Passenger.of("1"));
        assertTrue(elevator.isBinding(Passenger.of("1")));
        elevator.unbind(Passenger.of("1"));
        assertFalse(elevator.getPassengers().contains(Passenger.of("1")));
    }

    @Test
    void Given_In_When_IsMatched_Then_ReturnFalse() {
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(10), Floor.of(-1), null, Direction.STOP,
                ElevatorState.NONE, Map.of("1", mock(Request.class)), new ArrayList<>(), Set.of(Passenger.of("1")), null, new ArrayList<>(), new ArrayList<>());
        assertTrue(elevator.isMatched(Floor.of(-1), Floor.of(10)));
    }

    @Test
    void Given_Out_When_IsMatched_Then_ReturnTrue() {
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(10), Floor.of(-1), null, Direction.STOP,
                ElevatorState.NONE, new HashMap<>(), new ArrayList<>(), Set.of(Passenger.of("1")), null, new ArrayList<>(), new ArrayList<>());
        assertFalse(elevator.isMatched(Floor.of(-1), Floor.of(20)));
        assertFalse(elevator.isMatched(Floor.of(-6), Floor.of(7)));
    }

    @Test
    void Given_When_ReleaseDoor_Then_Expected() {
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), null, Direction.STOP,
                ElevatorState.COMPLETED_IN, new HashMap<>(), new ArrayList<>(), Set.of(Passenger.of("1")), Passenger.of("1"), new ArrayList<>(), new ArrayList<>());
        elevator.releaseDoor();
        assertNull(elevator.getNotified());
        assertEquals(ElevatorState.NONE, elevator.getState());
    }

    @Test
    void Given_ExistOneOut_When_NotifyPassengerOut_Then_Expected() {
        /**
         * 存在一个需要出梯的乘客
         */
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
                ElevatorState.NONE, requests, toBeNotified, Set.of(firstPassenger), null, onPassage, new ArrayList<>());
        elevator.notifyPassengerOut();

        assertEquals(ElevatorState.WAITING_OUT, elevator.getState());
        assertEquals(firstPassenger, elevator.getNotified());
        assertTrue(elevator.getToBeNotified().isEmpty());
        assertTrue(elevator.getTransferStation().isEmpty());
    }

    @Test
    void Given_ExistMoreThanOneOut_When_NotifyPassengerOut_Then_Expected() {
        /**
         * 存在三个需要出梯的乘客
         */
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
        List<Passenger> onPassage = new ArrayList<>();
        onPassage.add(firstPassenger);
        onPassage.add(secondPassenger);
        onPassage.add(thirdPassenger);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP,
                ElevatorState.NONE, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), null, onPassage, new ArrayList<>());
        elevator.notifyPassengerOut();

        assertEquals(ElevatorState.WAITING_OUT, elevator.getState());
        assertEquals(thirdPassenger, elevator.getNotified());
        assertEquals(2, elevator.getToBeNotified().size());
        assertTrue(elevator.getTransferStation().isEmpty());
    }

    @Test
    void Given_ExistMoreThanOneOut2_When_NotifyPassengerOut_Then_Expected() {
        /**
         * 存在三个需要出梯的乘客，但有一个不需要出梯的乘客在三个乘客之前
         */
        Passenger firstPassenger = Passenger.of("1");
        Passenger secondPassenger = Passenger.of("2");
        Passenger thirdPassenger = Passenger.of("3");
        Passenger fourthPassenger = Passenger.of("4");
        Map<String, Request> requests = new HashMap<>();
        Floor firstFloor = Floor.of(1);
        Floor fifthFloor = Floor.of(5);
        Request firstRequest = new Request(1L, firstPassenger, firstFloor, fifthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("1", firstRequest);
        Request secondRequest = new Request(2L, secondPassenger, firstFloor, fifthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("2", secondRequest);
        Request thirdRequest = new Request(3L, thirdPassenger, firstFloor, fifthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("3", thirdRequest);
        Request forthRequest = new Request(4L, fourthPassenger, Floor.of(2), Floor.of(10), Instant.now(), Instant.now(), null, null);
        requests.put("4", forthRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        List<Passenger> onPassage = new ArrayList<>();
        onPassage.add(firstPassenger);
        onPassage.add(secondPassenger);
        onPassage.add(thirdPassenger);
        onPassage.add(fourthPassenger);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP,
                ElevatorState.NONE, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), null, onPassage, new ArrayList<>());
        elevator.notifyPassengerOut();

        assertEquals(ElevatorState.WAITING_OUT, elevator.getState());
        assertEquals(fourthPassenger, elevator.getNotified());
        assertEquals(3, elevator.getToBeNotified().size());
        assertEquals(1, elevator.getTransferStation().size());
        assertEquals(fourthPassenger, elevator.getTransferStation().get(0));
    }

    @Test
    void Given_NoWaitingForPassenger_When_NotifyPassengerIn_Then_Expected() {
        /**
         * 没有需要乘梯的乘客
         */
        Map<String, Request> requests = new HashMap<>();
        Floor firstFloor = Floor.of(1);
        Floor fifthFloor = Floor.of(5);
        List<Passenger> toBeNotified = new ArrayList<>();
        List<Passenger> onPassage = new ArrayList<>();
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP,
                ElevatorState.NONE, requests, toBeNotified, Set.of(), null, onPassage, new ArrayList<>());
        elevator.notifyPassengerIn();

        assertEquals(ElevatorState.COMPLETED_IN, elevator.getState());
        assertTrue(elevator.getToBeNotified().isEmpty());
        assertTrue(elevator.getTransferStation().isEmpty());
    }

    @Test
    void Given_OnePassengerWaitingForElevator_When_NotifyPassengerIn_Then_Expected() {
        /**
         * 有一个需要乘梯的乘客
         */
        Passenger firstPassenger = Passenger.of("1");
        Passenger secondPassenger = Passenger.of("2");
        Passenger thirdPassenger = Passenger.of("3");
        Passenger fourthPassenger = Passenger.of("4");
        Map<String, Request> requests = new HashMap<>();
        Floor firstFloor = Floor.of(1);
        Floor fifthFloor = Floor.of(5);
        Request firstRequest = new Request(1L, firstPassenger, Floor.of(3), fifthFloor, Instant.now(), null, null, null);
        requests.put("1", firstRequest);
        Request secondRequest = new Request(2L, secondPassenger, firstFloor, fifthFloor, Instant.now(), null, null, null);
        requests.put("2", secondRequest);
        Request thirdRequest = new Request(3L, thirdPassenger, firstFloor, fifthFloor, Instant.now(), null, null, null);
        requests.put("3", thirdRequest);
        Request forthRequest = new Request(4L, fourthPassenger, Floor.of(2), Floor.of(10), null, Instant.now(), null, null);
        requests.put("4", forthRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        List<Passenger> onPassage = new ArrayList<>();
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(3), Direction.UP,
                ElevatorState.COMPLETED_OUT, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger, fourthPassenger), null, onPassage, new ArrayList<>());
        elevator.notifyPassengerIn();

        assertEquals(ElevatorState.WAITING_IN, elevator.getState());
        assertTrue(elevator.getToBeNotified().isEmpty());
        assertEquals(firstPassenger, elevator.getNotified());
        assertTrue(elevator.getTransferStation().isEmpty());
    }

    @Test
    void Given_MoreThanOnePassengerWaitingForElevatorUp_When_NotifyPassengerIn_Then_Expected() {
        /**
         * 有两个或者以上需要乘梯的乘客，通知进梯逻辑应该满足先到乘前提下，后下的先进
         */
        Passenger firstPassenger = Passenger.of("1");
        Passenger secondPassenger = Passenger.of("2");
        Passenger thirdPassenger = Passenger.of("3");
        Passenger fourthPassenger = Passenger.of("4");
        Map<String, Request> requests = new HashMap<>();
        Request firstRequest = new Request(1L, firstPassenger, Floor.of(3), Floor.of(4), Instant.now(), null, null, null);
        requests.put("1", firstRequest);
        Request secondRequest = new Request(2L, secondPassenger, Floor.of(3), Floor.of(5), Instant.now().minusSeconds(600), null, null, null);
        requests.put("2", secondRequest);
        Request thirdRequest = new Request(3L, thirdPassenger, Floor.of(3), Floor.of(7), Instant.now().minusSeconds(100), null, null, null);
        requests.put("3", thirdRequest);
        Request forthRequest = new Request(4L, fourthPassenger, Floor.of(2), Floor.of(10), null, Instant.now(), null, null);
        requests.put("4", forthRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        List<Passenger> onPassage = new ArrayList<>();
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), Floor.of(3), Direction.UP,
                ElevatorState.COMPLETED_OUT, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger, fourthPassenger), null, onPassage, new ArrayList<>());
        elevator.notifyPassengerIn();

        assertEquals(ElevatorState.WAITING_IN, elevator.getState());
        assertEquals(thirdPassenger, elevator.getNotified());
        assertEquals(1, elevator.getToBeNotified().size());
        assertTrue(elevator.getToBeNotified().contains(firstPassenger));
        assertTrue(elevator.getTransferStation().isEmpty());
    }


    @Test
    void Given_MoreThanOnePassengerWaitingForElevatorDown_When_NotifyPassengerIn_Then_Expected() {
        /**
         * 有两个或者以上需要乘梯的乘客，通知进梯逻辑应该满足先到乘前提下，后下的先进
         */
        Passenger firstPassenger = Passenger.of("1");
        Passenger secondPassenger = Passenger.of("2");
        Passenger thirdPassenger = Passenger.of("3");
        Passenger fourthPassenger = Passenger.of("4");
        Map<String, Request> requests = new HashMap<>();
        Request firstRequest = new Request(1L, firstPassenger, Floor.of(10), Floor.of(4), Instant.now(), null, null, null);
        requests.put("1", firstRequest);
        Request secondRequest = new Request(2L, secondPassenger, Floor.of(10), Floor.of(5), Instant.now().minusSeconds(600), null, null, null);
        requests.put("2", secondRequest);
        Request thirdRequest = new Request(3L, thirdPassenger, Floor.of(10), Floor.of(7), Instant.now().minusSeconds(100), null, null, null);
        requests.put("3", thirdRequest);
        Request forthRequest = new Request(4L, fourthPassenger, Floor.of(2), Floor.of(10), null, Instant.now(), null, null);
        requests.put("4", forthRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        List<Passenger> onPassage = new ArrayList<>();
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(20), Floor.of(10), Direction.DOWN,
                ElevatorState.COMPLETED_OUT, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger, fourthPassenger), null, onPassage, new ArrayList<>());
        elevator.notifyPassengerIn();

        assertEquals(ElevatorState.WAITING_IN, elevator.getState());
        assertEquals(firstPassenger, elevator.getNotified());
        assertEquals(1, elevator.getToBeNotified().size());
        assertTrue(elevator.getToBeNotified().contains(thirdPassenger));
        assertTrue(elevator.getTransferStation().isEmpty());
    }

    @Test
    void Given_MoreThanOnePassengerWaitingForElevatorDownAndTransferStationNotEmpty_When_NotifyPassengerIn_Then_Expected() {
        /**
         * 有两个或者以上需要乘梯的乘客，且中转站不为空，通知进梯逻辑应该满足先到乘前提下，后下的先进
         */
        Passenger firstPassenger = Passenger.of("1");
        Passenger secondPassenger = Passenger.of("2");
        Passenger thirdPassenger = Passenger.of("3");
        Passenger fourthPassenger = Passenger.of("4");
        Passenger fifthPassenger = Passenger.of("5");
        Map<String, Request> requests = new HashMap<>();
        Request firstRequest = new Request(1L, firstPassenger, Floor.of(10), Floor.of(4), Instant.now(), null, null, null);
        requests.put("1", firstRequest);
        Request secondRequest = new Request(2L, secondPassenger, Floor.of(10), Floor.of(5), Instant.now().minusSeconds(600), null, null, null);
        requests.put("2", secondRequest);
        Request thirdRequest = new Request(3L, thirdPassenger, Floor.of(10), Floor.of(7), Instant.now().minusSeconds(100), null, null, null);
        requests.put("3", thirdRequest);
        Request forthRequest = new Request(4L, fourthPassenger, Floor.of(2), Floor.of(10), null, Instant.now(), null, null);
        requests.put("4", forthRequest);
        Request fifthRequest = new Request(5L, fifthPassenger, Floor.of(12), Floor.of(6), null, Instant.now().minusSeconds(1000), null, null);
        requests.put("5", fifthRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        List<Passenger> onPassage = new ArrayList<>();
        List<Passenger> transferStation = new ArrayList<>();
        transferStation.add(Passenger.of("5"));
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(20), Floor.of(10), Direction.DOWN,
                ElevatorState.COMPLETED_OUT, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger, fourthPassenger, fifthPassenger), null, onPassage, transferStation);
        elevator.notifyPassengerIn();

        assertEquals(ElevatorState.WAITING_IN, elevator.getState());
        assertEquals(firstPassenger, elevator.getNotified());
        assertEquals(2, elevator.getToBeNotified().size());
        assertTrue(elevator.getToBeNotified().contains(thirdPassenger));
        assertTrue(elevator.getToBeNotified().contains(fifthPassenger));
        assertTrue(elevator.getTransferStation().isEmpty());
    }

    @Test
    void Given_NotNotifyPassenger_When_Finish_Then_Exception() {
        Passenger passenger = Passenger.of("100");
        assertThrows(IllegalStateException.class, () -> elevator.finish(passenger));
    }

    @Test
    void Given_ElevatorStateWaitingIn_When_Finish_Then_Expected() {
        Passenger firstPassenger = Passenger.of("1");
        Map<String, Request> requests = new HashMap<>();
        Request firstRequest = new Request(1L, firstPassenger, Floor.of(10), Floor.of(4), Instant.now(), null, null, null);
        requests.put("1", firstRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        List<Passenger> onPassage = new ArrayList<>();
        List<Passenger> transferStation = new ArrayList<>();
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(20), Floor.of(10), Direction.DOWN,
                ElevatorState.WAITING_IN, requests, toBeNotified, Set.of(firstPassenger), firstPassenger, onPassage, transferStation);
        elevator.finish(firstPassenger);
        assertTrue(onPassage.contains(firstPassenger));
        assertNotNull(firstRequest.getIn());
    }

    @Test
    void Given_ElevatorStateWaitingOut_When_Finish_Then_Expected() {
        Passenger firstPassenger = Passenger.of("1");
        Map<String, Request> requests = new HashMap<>();
        Request firstRequest = new Request(1L, firstPassenger, Floor.of(10), Floor.of(4), Instant.now(), Instant.now(), null, null);
        requests.put("1", firstRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        List<Passenger> onPassage = new ArrayList<>();
        List<Passenger> transferStation = new ArrayList<>();
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(20), Floor.of(10), Direction.DOWN,
                ElevatorState.WAITING_OUT, requests, toBeNotified, Set.of(firstPassenger), firstPassenger, onPassage, transferStation);
        RequestHistory history = elevator.finish(firstPassenger);
        assertTrue(requests.isEmpty());
        assertNotNull(firstRequest.getOut());
        assertNotNull(history);
    }


    @Test
    void Given_ElevatorStateNoneAndNotExistRequest_When_CancelRequest_Then_Exception() {
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), null, Direction.STOP,
                ElevatorState.COMPLETED_IN, new HashMap<>(), new ArrayList<>(), Set.of(Passenger.of("1")), Passenger.of("1"), new ArrayList<>(), new ArrayList<>());
        Passenger passenger = Passenger.of("2");
        assertThrows(RequestNotFoundException.class, () -> elevator.cancelRequest(passenger, "for test"));
    }

    @Test
    void Given_ElevatorStateNone_When_CancelRequestOfNotifiedPassenger_Then_Expected() {
        Passenger passenger = Passenger.of("2");
        Map<String, Request> requests = new HashMap<>();
        Floor first = Floor.of(1);
        Floor fifth = Floor.of(5);
        Request request = Request.create(passenger, first, fifth);
        requests.put("2", request);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), null, Direction.UP,
                ElevatorState.NONE, requests, new ArrayList<>(), Set.of(passenger), null, new ArrayList<>(), new ArrayList<>());
        RequestHistory history = elevator.cancelRequest(passenger, "for test");
        assertEquals(request, history.getRequest());
        assertNotNull(history.getArchivedOn());
    }

    @Test
    void Given_ElevatorStateWaitingOutAndExistRequestAndNoOutAndIn_When_CancelRequest_Then_Expected() {
        Passenger passenger = Passenger.of("2");
        Map<String, Request> requests = new HashMap<>();
        Floor first = Floor.of(1);
        Floor fifth = Floor.of(5);
        Request request = Request.create(passenger, first, fifth);
        requests.put("2", request);
        List<Passenger> onPassage = new ArrayList<>();
        onPassage.add(passenger);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), null, Direction.UP,
                ElevatorState.WAITING_OUT, requests, new ArrayList<>(), Set.of(passenger), passenger, onPassage, new ArrayList<>());
        RequestHistory history = elevator.cancelRequest(passenger, "for test");
        assertEquals(request, history.getRequest());
        assertNotNull(history.getArchivedOn());
        assertEquals(ElevatorState.COMPLETED_OUT, elevator.getState());
    }

    @Test
    void Given_ElevatorStateWaitingOutAndExistRequestAndNextOutAndNoIn_When_CancelRequest_Then_Expected() {
        Passenger firstPassenger = Passenger.of("1");
        Passenger secondPassenger = Passenger.of("2");
        Map<String, Request> requests = new HashMap<>();
        Floor firstFloor = Floor.of(1);
        Floor fifthFloor = Floor.of(5);
        Request firstRequest = Request.create(firstPassenger, firstFloor, fifthFloor);
        requests.put("1", firstRequest);
        Request secondRequest = Request.create(secondPassenger, Floor.of(-1), fifthFloor);
        requests.put("2", secondRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        toBeNotified.add(secondPassenger);
        List<Passenger> onPassage = new ArrayList<>();
        onPassage.add(secondPassenger);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP,
                ElevatorState.WAITING_OUT, requests, toBeNotified, Set.of(firstPassenger, secondPassenger), firstPassenger, onPassage, new ArrayList<>());
        RequestHistory history = elevator.cancelRequest(firstPassenger, "for test");
        assertEquals(firstRequest, history.getRequest());
        assertNotNull(history.getArchivedOn());
        assertEquals(ElevatorState.WAITING_OUT, elevator.getState());
        assertTrue(elevator.getOnPassage().isEmpty());
        assertEquals(secondPassenger, elevator.getNotified());
        assertTrue(elevator.getToBeNotified().isEmpty());
    }


}