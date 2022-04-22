package com.robustel.dispatching.domain.elevator;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.domain.robot.Robot;
import com.robustel.dispatching.domain.robot.RobotId;
import com.robustel.dispatching.domain.robot.RobotNotAllowedEnterException;
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
import static org.mockito.Mockito.*;

/**
 * @author YangXuehong
 * @date 2022/4/11
 */
class ElevatorTest {

    private Elevator outOfService;
    private Elevator elevator;
    private Map<String, Request> called;
    private Map<String, Request> took;
    private Set<RobotId> notified;

    @BeforeEach
    void init() {
        Set<RobotId> whiteList = new HashSet<>();
        RobotId robotId = RobotId.of("1");
        whiteList.add(robotId);
        called = new HashMap<>();
        took = new HashMap<>();
        notified = new HashSet<>();
        outOfService = new Elevator(ElevatorId.of("foo"), Floor.of(16), Floor.of(-1),
                State.OUT_OF_SERVICE, called, took, whiteList, notified);
        elevator = new Elevator(ElevatorId.of("foo"), Floor.of(16), Floor.of(-1),
                State.IN_SERVICE, called, took, whiteList, notified);
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
    void Given_RobotNotInWhiteList_When_Enter_Then_ThrowsRobotNotAllowedEnterException() {
        RobotId robotId = RobotId.of("2");
        Robot robot = mock(Robot.class);
        when(robot.getId()).thenReturn(robotId);
        Assertions.assertThrows(RobotNotAllowedEnterException.class,
                () -> elevator.enter(robot));

    }

    @Test
    void Given_RobotWithoutTakeElevator_When_Enter_Then_ThrowsRequestNotFoundException() {
        RobotId robotId = RobotId.of("1");
        Robot robot = mock(Robot.class);
        when(robot.getId()).thenReturn(robotId);
        Assertions.assertThrows(RequestNotFoundException.class,
                () -> elevator.enter(robot));

    }

    @Test
    void Given_RobotTakeElevator_When_Enter_Then_ThrowsRequestNotFoundException() {
        RobotId robotId = RobotId.of("1");
        Robot robot = mock(Robot.class);
        when(robot.getId()).thenReturn(robotId);
        Request request = Request.of(robotId, Floor.of(1), Floor.of(2));
        called.put(request.getRobotId().getValue(), request);
        notified.add(robotId);
        elevator.enter(robot);
        assertTrue(took.containsValue(request));
        verify(robot).enter(ElevatorId.of("foo"));
        assertFalse(notified.contains(robotId));
    }


    @Test
    void Given_RobotNotInElevator_When_Leave_Then_ThrowsRequestNotFoundException() {
        RobotId robotId = RobotId.of("1");
        Robot robot = mock(Robot.class);
        when(robot.getId()).thenReturn(robotId);
        Assertions.assertThrows(RequestNotFoundException.class,
                () -> elevator.leave(robot));
    }

    @Test
    void Given_RobotInElevator_When_Leave_Then_Success() {
        RobotId robotId = RobotId.of("1");
        Robot robot = mock(Robot.class);
        when(robot.getId()).thenReturn(robotId);
        Request request = Request.of(robotId, Floor.of(1), Floor.of(2));
        took.put(request.getRobotId().getValue(), request);
        notified.add(robotId);
        elevator.leave(robot);
        assertFalse(took.containsValue(request));
        verify(robot).leave(ElevatorId.of("foo"));
        assertFalse(notified.contains(robotId));
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
//        elevator.arrive(Floor.of(1));
//        assertTrue(robots.get("1"));
//        robots.clear();
//
//        elevator.arrive(Floor.of(2));
//        assertFalse(robots.get("2"));
//        robots.clear();
//
//        elevator.arrive(Floor.of(3));
//        assertTrue(robots.isEmpty());
//        robots.clear();
//
//        elevator.arrive(Floor.of(5));
//        assertTrue(robots.isEmpty());
//        robots.clear();
//
//        elevator.arrive(Floor.of(10));
//        assertFalse(robots.get("3"));
//        robots.clear();
//
//        elevator.arrive(Floor.of(9));
//        assertTrue(robots.isEmpty());
//        robots.clear();
//
//        elevator.arrive(Floor.of(8));
//        assertTrue(robots.isEmpty());
//        robots.clear();
//
//        elevator.arrive(Floor.of(8));
//        assertTrue(robots.isEmpty());
//        robots.clear();
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

    @Test
    void Given_RobotTakeElevator_When_Release_Then_CleanRobotRequest() {
        RobotId robotId = RobotId.of("1");
        Robot robot = mock(Robot.class);
        when(robot.getId()).thenReturn(robotId);
        Request request = Request.of(robotId, Floor.of(1), Floor.of(2));
        called.put(request.getRobotId().getValue(), request);
        notified.add(robotId);
        elevator.release(robotId);
        assertFalse(called.containsValue(request));
        assertFalse(notified.contains(robotId));
    }

}