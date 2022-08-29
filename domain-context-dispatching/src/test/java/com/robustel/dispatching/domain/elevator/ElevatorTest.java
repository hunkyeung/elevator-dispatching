package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.DomainException;
import com.robustel.ddd.service.EventPublisher;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.ddd.service.UidGenerator;
import com.robustel.dispatching.domain.requesthistory.RequestHistory;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ElevatorTest {

    private Elevator elevator;

    public static final MockedStatic<ServiceLocator> MOCKED_STATIC = mockStatic(ServiceLocator.class);

    @BeforeAll
    static void initAll() {
        MOCKED_STATIC.when(() -> ServiceLocator.service(UidGenerator.class)).thenReturn((UidGenerator) () -> 1);
        MOCKED_STATIC.when(() -> ServiceLocator.service(ElevatorController.class)).thenReturn((mock(ElevatorController.class)));
        MOCKED_STATIC.when(() -> ServiceLocator.service(PassengerController.class)).thenReturn(mock(PassengerController.class));
        MOCKED_STATIC.when(() -> ServiceLocator.service(EventPublisher.class)).thenReturn(mock(EventPublisher.class));
    }

    @AfterAll
    static void close() {
        MOCKED_STATIC.close();
    }


    @BeforeEach
    void init() {
        Set<Floor> pressedFloor = new HashSet<>();
        pressedFloor.add(Floor.of(2));
        pressedFloor.add(Floor.of(5));
        pressedFloor.add(Floor.of(10));

        elevator = new Elevator(1L, "foobar2000", Floor.of(10), Floor.of(-1), null, null, ElevatorState.NONE, new HashMap<>(), new ArrayList<>(), new HashSet<>(), null, new ArrayList<>(), new ArrayList<>(), pressedFloor);
    }

    @Test
    void Given_Normal_When_Create_Then_Expected() {
        assertNotNull(elevator.id());
        assertEquals("foobar2000", elevator.getName());
        assertEquals(Floor.of(10), elevator.getHighest());
        assertEquals(Floor.of(-1), elevator.getLowest());
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
    void Given_NullName_When_Create_Then_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Elevator.create(null, 10, 2));
    }

    @Test
    void Given_NonNullName_When_Create_Then_Expected() {
        Elevator elevator = Elevator.create("foobar2000", 10, 2);
        assertNotNull(elevator);
    }

    @Test
    void Given_NonNullName_When_CreateWithId_Then_Expected() {
        Elevator elevator = Elevator.create(0, "foobar2000", 2, 1);
        assertEquals(1, elevator.id());
    }

    @Test
    void Given_NullName_When_CreateWithId_Then_Expected() {
        assertThrows(NullPointerException.class, () -> Elevator.create(0, null, 2, 1));
    }

    @Test
    void Given_NonZeroId_When_Create_Then_IdWhatYouGave() {
        Elevator elevator = Elevator.create(1, "foobar2000", 2, 1);
        assertEquals(1, elevator.id());
    }

    @Test
    void Given_HighestLessThanLowest_When_Create_When_Exception() {
        assertThrows(DomainException.class, () -> Elevator.create("foobar2000", -2, 10));
    }

    @Test
    void Given_Null_When_Arrive_Then_ThrowNullPointerException() {
        Floor floor2 = Floor.of(2);
        assertThrows(NullPointerException.class, () -> elevator.arrive(null, Direction.UP));
        assertThrows(NullPointerException.class, () -> elevator.arrive(floor2, null));
    }

    @Test
    void Given_FloorAndDirection_When_Arrive_Then_CurrentFloorAndDirectionWereSet() {
        Floor floor2 = Floor.of(2);
        assertTrue(elevator.getPressedFloor().contains(floor2));
        elevator.arrive(floor2, Direction.UP);
        assertNull(elevator.getCurrentFloor());
        assertNull(elevator.getNextDirection());
        assertFalse(elevator.getPressedFloor().contains(floor2));
    }


    @Test
    void Given_NullParameter_When_Take_Then_ThrowNullPointerException() {
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), null, Direction.STOP, ElevatorState.NONE, new HashMap<>(), new ArrayList<>(), Set.of(), null, new ArrayList<>(), new ArrayList<>(), new HashSet<>());
        assertThrows(NullPointerException.class, () -> elevator.take(null, null, null));
        Passenger passenger = Passenger.of("1");
        Floor floor = Floor.of(1);
        assertThrows(NullPointerException.class, () -> elevator.take(passenger, null, null));
        assertThrows(NullPointerException.class, () -> elevator.take(passenger, floor, null));
    }

    @Test
    void Given_RequestOfPassengerExist_When_Take_Then_ThrowsException() {
        Passenger passenger = Passenger.of("1");
        Floor first = Floor.of(1);
        Floor fifth = Floor.of(5);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), null, Direction.STOP, ElevatorState.NONE, new HashMap<>(), new ArrayList<>(), Set.of(passenger), null, new ArrayList<>(), new ArrayList<>(), new HashSet<>());
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
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), null, Direction.STOP, ElevatorState.NONE, requests, new ArrayList<>(), Set.of(passenger), null, new ArrayList<>(), new ArrayList<>(), new HashSet<>());
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
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), null, Direction.STOP, ElevatorState.NONE, requests, new ArrayList<>(), Set.of(passenger), null, new ArrayList<>(), new ArrayList<>(), new HashSet<>());
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
    void Given_NullPassenger_When_Bind_Then_ThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> elevator.bind(null));
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
    void Given_NullPassenger_When_Unbind_Then_NullPointerException() {
        assertThrows(NullPointerException.class, () -> elevator.unbind(null));
    }

    @Test
    void Given_BindingPassenger_When_Unbind_Then_Ignore() {
        elevator.bind(Passenger.of("1"));
        assertTrue(elevator.isBinding(Passenger.of("1")));
        elevator.unbind(Passenger.of("1"));
        assertFalse(elevator.getBinding().contains(Passenger.of("1")));
    }

    @Test
    void Given_FromOrToGreaterThanHighest_When_IsMatched_Then_ReturnFalse() {
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(10), Floor.of(-1), null, Direction.STOP, ElevatorState.NONE, Map.of("1", mock(Request.class)), new ArrayList<>(), Set.of(Passenger.of("1")), null, new ArrayList<>(), new ArrayList<>(), new HashSet<>());
        assertFalse(elevator.isMatched(Floor.of(11), Floor.of(2)));
        assertFalse(elevator.isMatched(Floor.of(5), Floor.of(20)));
        assertFalse(elevator.isMatched(Floor.of(50), Floor.of(20)));
    }

    @Test
    void Given_FromOrToLowerThanLowest_When_IsMatched_Then_ReturnFalse() {
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(10), Floor.of(-1), null, Direction.STOP, ElevatorState.NONE, Map.of("1", mock(Request.class)), new ArrayList<>(), Set.of(Passenger.of("1")), null, new ArrayList<>(), new ArrayList<>(), new HashSet<>());
        assertFalse(elevator.isMatched(Floor.of(-2), Floor.of(-20)));
        assertFalse(elevator.isMatched(Floor.of(1), Floor.of(-20)));
        assertFalse(elevator.isMatched(Floor.of(-2), Floor.of(-1)));
    }

    @Test
    void Given_FromAndToBetweenHighestAndLowest_When_IsMatched_Then_ReturnTrue() {
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(10), Floor.of(-1), null, Direction.STOP, ElevatorState.NONE, new HashMap<>(), new ArrayList<>(), Set.of(Passenger.of("1")), null, new ArrayList<>(), new ArrayList<>(), new HashSet<>());
        assertTrue(elevator.isMatched(Floor.of(-1), Floor.of(10)));
        assertTrue(elevator.isMatched(Floor.of(10), Floor.of(-1)));
        assertTrue(elevator.isMatched(Floor.of(1), Floor.of(5)));
        assertTrue(elevator.isMatched(Floor.of(7), Floor.of(3)));
    }

    @Test
    void Given_When_ReleaseDoor_Then_Expected() {
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), null, Direction.STOP, ElevatorState.NONE, new HashMap<>(), new ArrayList<>(), Set.of(Passenger.of("1")), Passenger.of("1"), new ArrayList<>(), new ArrayList<>(), new HashSet<>());
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
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP, ElevatorState.NONE, requests, toBeNotified, Set.of(firstPassenger), null, onPassage, new ArrayList<>(), new HashSet<>());
        Passenger of = Passenger.of("100");
        Assertions.assertThrows(Elevator.RequestNotFoundException.class, () -> elevator.cancelRequest(of, ""));
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
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP, ElevatorState.NONE, requests, toBeNotified, Set.of(firstPassenger), null, onPassage, new ArrayList<>(), new HashSet<>());
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
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP, ElevatorState.NONE, requests, toBeNotified, Set.of(firstPassenger), null, new ArrayList<>(), new ArrayList<>(), new HashSet<>());
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
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP, ElevatorState.WAITING_OUT, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), firstPassenger, onPassage, new ArrayList<>(), new HashSet<>());
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
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP, ElevatorState.WAITING_OUT, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), firstPassenger, onPassage, new ArrayList<>(), new HashSet<>());
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
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP, ElevatorState.WAITING_OUT, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), firstPassenger, onPassage, new ArrayList<>(), new HashSet<>());
        elevator.cancelRequest(thirdPassenger, "for test");
        assertEquals(ElevatorState.WAITING_OUT, elevator.getState());
        assertFalse(elevator.getRequests().containsValue(thirdRequest));
        assertEquals(2, onPassage.size());
        assertEquals(firstPassenger, elevator.getNotified());
        assertEquals(1, elevator.getToBeNotified().size());
    }

    @Test
    void Given_NoneStateMode_When_Finish_Then_ThrowsRequestFinishedNotAllowedException() {
        Passenger firstPassenger = Passenger.of("1");
        Map<String, Request> requests = new HashMap<>();
        Floor firstFloor = Floor.of(1);
        Floor fifthFloor = Floor.of(5);
        Request firstRequest = new Request(1L, firstPassenger, firstFloor, fifthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("1", firstRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        List<Passenger> onPassage = new ArrayList<>();
        onPassage.add(firstPassenger);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP, ElevatorState.NONE, requests, toBeNotified, Set.of(firstPassenger), null, onPassage, new ArrayList<>(), new HashSet<>());
        Passenger of = Passenger.of("100");
        Assertions.assertThrows(Elevator.RequestFinishedNotAllowedException.class, () -> elevator.finish(of));
    }

    @Test
    void Given_WaitingOutState_When_FinishWithoutToBeNotified_Then_Expected() {
        Passenger firstPassenger = Passenger.of("1");
        Passenger secondPassenger = Passenger.of("2");
        Passenger thirdPassenger = Passenger.of("3");
        Passenger fourthPassenger = Passenger.of("4");
        Map<String, Request> requests = new HashMap<>();
        Floor firstFloor = Floor.of(1);
        Floor fifthFloor = Floor.of(5);
        Floor sixthFloor = Floor.of(6);
        Request firstRequest = new Request(1L, firstPassenger, firstFloor, fifthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("1", firstRequest);
        Request secondRequest = new Request(2L, secondPassenger, firstFloor, fifthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("2", secondRequest);
        Request thirdRequest = new Request(3L, thirdPassenger, firstFloor, fifthFloor, Instant.now(), null, null, null);
        requests.put("3", thirdRequest);
        Request fourthRequest = new Request(4L, thirdPassenger, firstFloor, sixthFloor, Instant.now(), Instant.now(), null, null);
        requests.put("4", fourthRequest);
        List<Passenger> toBeNotified = new ArrayList<>();
        toBeNotified.add(secondPassenger);
        List<Passenger> onPassage = new ArrayList<>();
        onPassage.add(firstPassenger);
        onPassage.add(secondPassenger);
        onPassage.add(fourthPassenger);
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), null, null, ElevatorState.WAITING_OUT, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), null, onPassage, new ArrayList<>(), new HashSet<>());
        elevator.arrive(Floor.of(5), Direction.DOWN);
        assertTrue(elevator.getTransferPassengers().contains(fourthPassenger));

    }

    @Test
    void Given_WaitingOutState_When_FinishWithoutNotified_Then_ThrowsRequestFinishedNotAllowedException() {
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
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP, ElevatorState.WAITING_OUT, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), firstPassenger, onPassage, new ArrayList<>(), new HashSet<>());
        Assertions.assertThrows(Elevator.RequestFinishedNotAllowedException.class, () -> elevator.finish(thirdPassenger));

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
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP, ElevatorState.WAITING_OUT, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), firstPassenger, onPassage, new ArrayList<>(), new HashSet<>());
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
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), firstFloor, Direction.UP, ElevatorState.WAITING_IN, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), firstPassenger, onPassage, new ArrayList<>(), new HashSet<>());
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
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP, ElevatorState.WAITING_IN, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), firstPassenger, onPassage, new ArrayList<>(), new HashSet<>());
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
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP, ElevatorState.WAITING_IN, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), firstPassenger, onPassage, new ArrayList<>(), new HashSet<>());
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
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP, ElevatorState.WAITING_IN, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), firstPassenger, onPassage, new ArrayList<>(), new HashSet<>());
        Assertions.assertThrows(Elevator.RequestFinishedNotAllowedException.class, () -> elevator.finish(thirdPassenger));

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
        Elevator elevator = new Elevator(1L, "foobar2000", Floor.of(-1), Floor.of(10), fifthFloor, Direction.UP, ElevatorState.WAITING_IN, requests, toBeNotified, Set.of(firstPassenger, secondPassenger, thirdPassenger), firstPassenger, onPassage, new ArrayList<>(), new HashSet<>());
        Optional<RequestHistory> firstRequestHistory = elevator.finish(firstPassenger);
        assertTrue(firstRequestHistory.isEmpty());
        assertEquals(ElevatorState.WAITING_IN, elevator.getState());
        assertEquals(secondPassenger, elevator.getNotified());
        assertTrue(onPassage.contains(firstPassenger));
        assertEquals(0, toBeNotified.size());

    }

}