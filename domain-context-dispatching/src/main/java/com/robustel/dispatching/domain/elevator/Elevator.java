package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.AbstractEntity;
import com.robustel.ddd.service.EventPublisher;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.dispatching.domain.robot.Robot;
import com.robustel.dispatching.domain.robot.RobotId;
import com.robustel.dispatching.domain.robot.RobotNotAllowedEnterException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author YangXuehong
 * @date 2022/4/8
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
@Slf4j
public class Elevator extends AbstractEntity<ElevatorId> {
    private Floor highest;//最高楼层
    private Floor lowest;//最低楼层
    private State state;
    private Map<String, Request> calledRequests;//招唤中的请求
    private Map<String, Request> tookRequests;//乘梯中请求
    private Set<RobotId> whiteList; //
    private Set<RobotId> notified; //当目标楼层到达时，已通知的机器人会进入该集合。机器响应（包括进梯、出梯或者主动释放开门）都会移出。集合为0时，释放开门

    public Elevator(ElevatorId id, Floor highest, Floor lowest, State state, Map<String, Request> calledRequests, Map<String, Request> tookRequests,
                    Set<RobotId> whiteList, Set<RobotId> notified) {
        super(id);
        this.highest = highest;
        this.lowest = lowest;
        this.state = state;
        this.calledRequests = calledRequests;
        this.tookRequests = tookRequests;
        this.whiteList = whiteList;
        this.notified = notified;
    }

    public static Elevator of(ElevatorId elevatorId, Floor highest, Floor lowest) {
        return new Elevator(elevatorId, highest, lowest, State.IN_SERVICE, new HashMap<>(), new HashMap<>(),
                new HashSet<>(0), new HashSet<>(0));
    }

    public void accept(Request request) {
        if (State.OUT_OF_SERVICE.equals(this.state)) {
            throw new ElevatorOutOfServiceException(id());
        }
        if (this.calledRequests.get(request.getRobotId().value()) != null || this.tookRequests.get(request.getRobotId().value()) != null) {
            throw new RequestAlreadyExistException(request.getRobotId(), id());
        }
        this.calledRequests.put(request.getRobotId().value(), request);
        log.debug("等待调度的乘梯请求:{}", this.calledRequests);
        ServiceLocator.service(EventPublisher.class).publish(new RequestSummitedEvent(id(), request));
    }

    // 当电梯到达时，通知相关机器人进出电梯
    public void arrive(Floor floor) {
        //先出后进
        noticeRobotToLeave(floor);
        noticeRobotToEnter(floor);
        if (this.notified.isEmpty()) {
            ServiceLocator.service(EventPublisher.class).publish(new ElevatorDoorReleasedEvent(id()));
        }
    }

    private void noticeRobotToEnter(Floor floor) {
        //通知机器人进梯
        //todo 后续针对多机一梯，多机多梯时，不能通知所有电梯，而是根据排队通知，如果允许一次多机乘梯时，还需要考虑先进后出，后进进先出原则
        log.debug("正在通知机器人进电梯【{}】...", id());
        Optional.ofNullable(this.calledRequests).orElse(new HashMap<>()).values().forEach(
                request -> {
//                    if (request.matchFrom(floor, direction)) {
                    if (request.matchFrom(floor)) {
                        ServiceLocator.service(EventPublisher.class).publish(new ElevatorArrivedEvent(request.getRobotId(), true));
                        this.notified.add(request.getRobotId());
                    }
                }
        );
    }

    private void noticeRobotToLeave(Floor floor) {
        //通知机器人出梯
        log.debug("正在通知机器人出电梯【{}】...", id());
        Optional.ofNullable(this.tookRequests).orElse(new HashMap<>()).values().forEach(
                request -> {
                    if (request.matchTo(floor)) {
                        ServiceLocator.service(EventPublisher.class).publish(new ElevatorArrivedEvent(request.getRobotId(), false));
                        this.notified.add(request.getRobotId());
                    }
                }
        );
    }

    public boolean isValid(Floor... floors) {
        return Arrays.stream(floors).anyMatch(floor -> floor.compareTo(lowest) >= 0 && floor.compareTo(highest) <= 0);
    }

    public void cancelRequest(RobotId robotId) {
        //todo 如果机器人无法出梯，且确保安全后，释放电梯，梯控请求是否仍保留
        Request request = this.calledRequests.remove(robotId.value());
        if (request != null) {//当机器人主动释放电梯时，表示机器人乘梯失败。如果需要继续乘梯，需重新招唤电梯
            respondFrom(robotId);
        } else {
            log.warn("找不到该机器人【" + robotId + "】搭乘此电梯【" + id() + "】，系统将忽略该请求");
        }
    }

    private void respondFrom(RobotId robotId) {
        this.notified.remove(robotId);
        if (this.notified.isEmpty()) {
            ServiceLocator.service(EventPublisher.class).publish(new ElevatorDoorReleasedEvent(id()));
        }
    }

    public void unbind(RobotId robotId) {
        if (!this.whiteList.remove(robotId)) {
            log.warn("机器人【{}】与该电梯【{}】未存在绑定关系", robotId, id());
        }

    }

    public void bind(RobotId robotId) {
        if (!this.whiteList.add(robotId)) {
            log.warn("机器人【{}】已经绑定到这台电梯【{}】", robotId, id());
        }
    }

    public void enter(Robot robot) {
        if (!canEnter(robot.id())) {
            throw new RobotNotAllowedEnterException(robot.id(), id());
        }
        Request request = this.calledRequests.remove(robot.id().value());
        if (request == null) {
            log.warn("找不到该机器【{}】乘梯【{}】请求", robot.id(), id());
            throw new RequestNotFoundException(robot.id(), id());
        }
        this.tookRequests.put(request.getRobotId().value(), request);
        robot.enter(id());
        respondFrom(robot.id());
    }

    public boolean canEnter(RobotId robotId) {
        return this.whiteList.contains(robotId);
    }

    public void leave(Robot robot) {
        Request request = this.tookRequests.remove(robot.id().value());
        if (request == null) {
            log.warn("找不到该机器【{}】乘梯【{}】请求", robot.id(), id());
            throw new RequestNotFoundException(robot.id(), id());
        }
        robot.leave(id());
        ServiceLocator.service(EventPublisher.class).publish(new RobotLeftEvent(id(), request));
        respondFrom(robot.id());
    }
}
