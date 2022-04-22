package com.robustel.dispatching.domain.elevator;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.domain.robot.RobotId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

    private Elevator outOfService;
    private Elevator elevator;
    private Map<String, Request> called;
    private Map<String, Request> took;

    @BeforeEach
    void init() {
        Set<RobotId> whiteList = new HashSet<>();
        RobotId robotId = RobotId.of("1");
        whiteList.add(robotId);
        called = new HashMap<>();
        took = new HashMap<>();
        outOfService = new Elevator(ElevatorId.of("foo"), Floor.of(16), Floor.of(-1),
                State.OUT_OF_SERVICE, called, took, whiteList, new HashSet<>());
        elevator = new Elevator(ElevatorId.of("foo"), Floor.of(16), Floor.of(-1),
                State.IN_SERVICE, called, took, whiteList, new HashSet<>());
    }

    @Test
    void Given_OutOfService_When_Accept_Then_ThrowsIllegalStateException() {
        Assertions.assertThrows(ElevatorOutOfServiceException.class,
                () -> outOfService.accept(mock(Request.class)));
    }

    @Test
    void Given_WithCalledRequest_When_Accept_Then_ThrowsIllegalStateException() {
        Request request = Request.of(RobotId.of("1"), Floor.of(2), Floor.of(10));
        Request mock = mock(Request.class);
        when(mock.getRobotId()).thenReturn(RobotId.of("1"));
        called.put("1", request);
        Assertions.assertThrows(RequestAlreadyExistException.class,
                () -> elevator.accept(mock));
    }

    @Test
    void Given_WithTookRequest_When_Accept_Then_ThrowsIllegalStateException() {
        Request request = Request.of(RobotId.of("1"), Floor.of(2), Floor.of(10));
        Request mock = mock(Request.class);
        when(mock.getRobotId()).thenReturn(RobotId.of("1"));
        took.put("1", request);
        Assertions.assertThrows(RequestAlreadyExistException.class,
                () -> elevator.accept(mock));
    }

    @Test
    void Given_WithNonRequest_When_Accept_Then_RequestInCalledMap() {
        Elevator elevator = Elevator.of(ElevatorId.of("foo"), Floor.of(16), Floor.of(-1));
        Request request = Request.of(RobotId.of("1"), Floor.of(1), Floor.of(2));
        elevator.accept(request);
        assertEquals(request, elevator.getCalledRequests().get(RobotId.of("1").getValue()));
    }

    @Test
    void Given_RobotWaitForElevator_When_Enter_Then_RequestInTookMap() {
        RobotId robotId = RobotId.of("1");
        Request request = Request.of(robotId, Floor.of(1), Floor.of(2));
        called.put(request.getRobotId().getValue(), request);
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
        Assertions.assertThrows(RequestNotFoundException.class,
                () -> elevator.enter(RobotId.of("2")));
    }

    @Test
    void Given_RobotInElevator_When_Leave_Then_RequestInTookMap() {
        RobotId robotId = RobotId.of("1");
        Request request = Request.of(robotId, Floor.of(1), Floor.of(2));
        took.put(request.getRobotId().getValue(), request);
        elevator.leave(robotId);
        assertNull(elevator.getTookRequests().get(robotId.getValue()));
    }


    @Test
    void Given_RobotNotInElevator_When_Leave_Then_ThrowsIllegalStateException() {
        RobotId robotId = RobotId.of("1");
        Request request = Request.of(robotId, Floor.of(1), Floor.of(2));
        took.put(request.getRobotId().getValue(), request);
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
        called.put("1", Request.of(RobotId.of("1"), Floor.of(1), Floor.of(2)));
        called.put("2", Request.of(RobotId.of("2"), Floor.of(5), Floor.of(2)));

        took.put("3", Request.of(RobotId.of("3"), Floor.of(5), Floor.of(10)));
        took.put("4", Request.of(RobotId.of("4"), Floor.of(8), Floor.of(2)));
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
        RobotId robotId = RobotId.of("2");
        elevator.bind(robotId);
        assertTrue(elevator.getWhiteList().contains(robotId));
    }

    @Test
    void Given_RobotId_When_Unbind_The_RemoveFromWhiteList() {
        RobotId robotId = RobotId.of("1");
        assertTrue(elevator.getWhiteList().contains(robotId));
        elevator.unbind(robotId);
        assertFalse(elevator.getWhiteList().contains(robotId));
    }

}