package com.robustel.dispatching.domain.elevator;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.domain.robot.RobotId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yeung.api.util.ServiceLocator;
import org.yeung.core.EventPublisher;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author YangXuehong
 * @date 2022/4/11
 */
class ElevatorTest {
    @Test
    void Given_OutOfService_When_Response_Then_ThrowsIllegalStateException() {
        Elevator elevator = new Elevator(ElevatorId.of("foo"), Floor.of(16), Floor.of(-1),
                State.OUT_OF_SERVICE, new HashMap<>(), new HashMap<>(), new HashSet<>());
        Assertions.assertThrows(ElevatorOutOfServiceException.class,
                () -> elevator.response(mock(Request.class)));
    }

    @Test
    void Given_WithCalledRequest_When_Response_Then_ThrowsIllegalStateException() {
        Request request = Request.of(RobotId.of("1"), Floor.of(2), Floor.of(10));
        Request mock = mock(Request.class);
        when(mock.getRobotId()).thenReturn(RobotId.of("1"));
        Map<String, Request> requestMap = new HashMap<>();
        requestMap.put("1", request);
        Elevator elevator = new Elevator(ElevatorId.of("foo"), Floor.of(16), Floor.of(-1),
                State.IN_SERVICE, requestMap, new HashMap<>(), new HashSet<>());
        Assertions.assertThrows(RequestAlreadyExistException.class,
                () -> elevator.response(mock));
    }

    @Test
    void Given_WithTookRequest_When_Response_Then_ThrowsIllegalStateException() {
        Request request = Request.of(RobotId.of("1"), Floor.of(2), Floor.of(10));
        Request mock = mock(Request.class);
        when(mock.getRobotId()).thenReturn(RobotId.of("1"));
        Map<String, Request> requestMap = new HashMap<>();
        requestMap.put("1", request);
        Elevator elevator = new Elevator(ElevatorId.of("foo"), Floor.of(16), Floor.of(-1),
                State.IN_SERVICE, new HashMap<>(), requestMap, new HashSet<>());
        Assertions.assertThrows(RequestAlreadyExistException.class,
                () -> elevator.response(mock));
    }

    @Test
    void Given_WithNonRequest_When_Response_Then_RequestInCalledMap() {
        Elevator elevator = Elevator.of(ElevatorId.of("foo"), Floor.of(16), Floor.of(-1));
        Request request = Request.of(RobotId.of("1"), Floor.of(1), Floor.of(2));
        elevator.response(request);
        assertEquals(request, elevator.getCalledRequests().get(RobotId.of("1").getValue()));
    }

    @Test
    void Given_RobotWaitForElevator_When_Enter_Then_RequestInTookMap() {
        Map<String, Request> calledRequests = new HashMap<>();
        RobotId robotId = RobotId.of("1");
        Request request = Request.of(robotId, Floor.of(1), Floor.of(2));
        calledRequests.put(request.getRobotId().getValue(), request);
        Elevator elevator = new Elevator(ElevatorId.of("foo"), Floor.of(16), Floor.of(-1),
                State.IN_SERVICE, calledRequests, new HashMap<>(), new HashSet<>());
        elevator.enter(robotId);
        assertEquals(request, elevator.getTookRequests().get(robotId.getValue()));
        assertNull(elevator.getCalledRequests().get(robotId.getValue()));
    }


    @Test
    void Given_RobotWaitForElevator_When_EnterWithNonRobotId_Then_ThrowsIllegalStateException() {
        Map<String, Request> calledRequests = new HashMap<>();
        RobotId robotId = RobotId.of("1");
        Request request = Request.of(robotId, Floor.of(1), Floor.of(2));
        calledRequests.put(request.getRobotId().getValue(), request);
        Elevator elevator = new Elevator(ElevatorId.of("foo"), Floor.of(16), Floor.of(-1),
                State.IN_SERVICE, calledRequests, new HashMap<>(), new HashSet<>());
        Assertions.assertThrows(RequestNotFoundException.class,
                () -> elevator.enter(RobotId.of("2")));
    }

    @Test
    void Given_RobotInElevator_When_Leave_Then_RequestInTookMap() {
        Map<String, Request> tookRequests = new HashMap<>();
        RobotId robotId = RobotId.of("1");
        Request request = Request.of(robotId, Floor.of(1), Floor.of(2));
        tookRequests.put(request.getRobotId().getValue(), request);
        Elevator elevator = new Elevator(ElevatorId.of("foo"), Floor.of(16), Floor.of(-1),
                State.IN_SERVICE, new HashMap<>(), tookRequests, new HashSet<>());
        elevator.leave(robotId);
        assertNull(elevator.getTookRequests().get(robotId.getValue()));
    }


    @Test
    void Given_RobotNotInElevator_When_Leave_Then_ThrowsIllegalStateException() {
        Map<String, Request> tookRequests
                = new HashMap<>();
        RobotId robotId = RobotId.of("1");
        Request request = Request.of(robotId, Floor.of(1), Floor.of(2));
        tookRequests.put(request.getRobotId().getValue(), request);
        Elevator elevator = new Elevator(ElevatorId.of("foo"), Floor.of(16), Floor.of(-1), State.IN_SERVICE,
                new HashMap<>(), tookRequests, new HashSet<>());
        Assertions.assertThrows(RequestNotFoundException.class,
                () -> elevator.leave(RobotId.of("2")));
    }

    @Test
    void Given_OverHighestFloorOrLowest_When_IsValid_Then_ReturnFalse() {
        Elevator elevator = Elevator.of(ElevatorId.of("1"), Floor.of(10), Floor.of(-2));
        assertFalse(elevator.isValid(Floor.of(-3)));
        assertFalse(elevator.isValid(Floor.of(11)));
    }


    @Test
    void Given_BetweenHighestFloorAndLowest_When_IsValid_Then_ReturnTrue() {
        Elevator elevator = Elevator.of(ElevatorId.of("1"), Floor.of(10), Floor.of(-2));
        assertTrue(elevator.isValid(Floor.of(-2)));
        assertTrue(elevator.isValid(Floor.of(5)));
        assertTrue(elevator.isValid(Floor.of(10)));
    }

    @Test
    void Given_Request_When_Arrive_Then_NoticeRobotToEnter() {
        Map<String, Boolean> robots = new HashMap<>();
        class ListenerEvent {
            @Subscribe
            void listener(ElevatorArrivedEvent event) {
                robots.put(event.getRobotId().getValue(), event.isEnterOrLeave());
            }
        }
        ServiceLocator.getService(EventPublisher.class).register(new ListenerEvent());
        Map<String, Request> called = new HashMap<>();
        called.put("1", Request.of(RobotId.of("1"), Floor.of(1), Floor.of(2)));
        called.put("2", Request.of(RobotId.of("2"), Floor.of(5), Floor.of(2)));

        Map<String, Request> took = new HashMap<>();
        took.put("3", Request.of(RobotId.of("3"), Floor.of(5), Floor.of(10)));
        took.put("4", Request.of(RobotId.of("4"), Floor.of(8), Floor.of(2)));
        Elevator elevator = new Elevator(ElevatorId.of("foo"), Floor.of(16), Floor.of(-1), State.IN_SERVICE, called, took,
                new HashSet<>());

        elevator.arrive(Floor.of(1), Direction.UP);
        assertTrue(robots.get("1"));
        robots.clear();

        elevator.arrive(Floor.of(2), Direction.UP);
        assertFalse(robots.get("4"));
        robots.clear();

        elevator.arrive(Floor.of(3), Direction.UP);
        assertTrue(robots.isEmpty());
        robots.clear();

        elevator.arrive(Floor.of(5), Direction.UP);
        assertTrue(robots.isEmpty());
        robots.clear();

        elevator.arrive(Floor.of(10), Direction.DOWN);
        assertFalse(robots.get("3"));
        robots.clear();

        elevator.arrive(Floor.of(9), Direction.DOWN);
        assertTrue(robots.isEmpty());
        robots.clear();

        elevator.arrive(Floor.of(8), Direction.DOWN);
        assertTrue(robots.isEmpty());
        robots.clear();

        elevator.arrive(Floor.of(8), Direction.DOWN);
        assertTrue(robots.isEmpty());
        robots.clear();
    }

    @Test
    void Given_RobotId_When_Bind_The_AddInWhiteList() {
        Map<String, Request> called = new HashMap<>();
        Map<String, Request> took = new HashMap<>();
        Elevator elevator = new Elevator(ElevatorId.of("foo"), Floor.of(16), Floor.of(-1), State.IN_SERVICE, called, took,
                new HashSet<>());
        RobotId robotId = RobotId.of("1");
        elevator.bind(robotId);
        assertTrue(elevator.getWhiteList().contains(robotId));
    }

    @Test
    void Given_RobotId_When_Unbind_The_RemoveFromWhiteList() {
        Map<String, Request> called = new HashMap<>();
        Map<String, Request> took = new HashMap<>();
        Set<RobotId> whiteList = new HashSet<>();
        RobotId robotId = RobotId.of("1");
        whiteList.add(robotId);
        Elevator elevator = new Elevator(ElevatorId.of("foo"), Floor.of(16), Floor.of(-1), State.IN_SERVICE, called, took,
                whiteList);
        assertTrue(elevator.getWhiteList().contains(robotId));
        elevator.unbind(robotId);
        assertFalse(elevator.getWhiteList().contains(robotId));
    }

}