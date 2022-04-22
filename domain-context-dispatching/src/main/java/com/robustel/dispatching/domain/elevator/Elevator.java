package com.robustel.dispatching.domain.elevator;

import com.robustel.dispatching.domain.robot.Robot;
import com.robustel.dispatching.domain.robot.RobotId;
import com.robustel.dispatching.domain.robot.RobotNotAllowedEnterException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.yeung.api.AbstractEntity;
import org.yeung.api.util.DomainEventPublisher;

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
            throw new ElevatorOutOfServiceException(getId());
        }
        if (this.calledRequests.get(request.getRobotId().getValue()) != null || this.tookRequests.get(request.getRobotId().getValue()) != null) {
            throw new RequestAlreadyExistException(request.getRobotId(), getId());
        }
        this.calledRequests.put(request.getRobotId().getValue(), request);
        log.info("等待调度的乘梯请求:{}", this.calledRequests);
        DomainEventPublisher.publish(new RequestSummitedEvent(getId(), request));
    }

    // 当电梯到达时，通知相关机器人进出电梯
    public void arrive(Floor floor, Direction direction) {
        //先出后进
        noticeRobotToLeave(floor);
        noticeRobotToEnter(floor, direction);
        if (this.notified.isEmpty()) {
            DomainEventPublisher.publish(new ElevatorDoorReleasedEvent(getId()));
        }
    }

    private void noticeRobotToEnter(Floor floor, Direction direction) {
        //通知机器人进梯
        //todo 后续针对多机一梯，多机多梯时，不能通知所有电梯，而是根据排队通知，如果允许一次多机乘梯时，还需要考虑先进后出，后进进先出原则
        log.info("正在通知机器人进电梯【{}】...", getId());
        Optional.ofNullable(this.calledRequests).orElse(new HashMap<>()).values().forEach(
                request -> {
                    if (request.matchFrom(floor, direction)) {
                        DomainEventPublisher.publish(new ElevatorArrivedEvent(request.getRobotId(), true));
                        this.notified.add(request.getRobotId());
                    }
                }
        );
    }

    private void noticeRobotToLeave(Floor floor) {
        //通知机器人出梯
        log.info("正在通知机器人出电梯【{}】...", getId());
        Optional.ofNullable(this.tookRequests).orElse(new HashMap<>()).values().forEach(
                request -> {
                    if (request.matchTo(floor)) {
                        DomainEventPublisher.publish(new ElevatorArrivedEvent(request.getRobotId(), false));
                        this.notified.add(request.getRobotId());
                    }
                }
        );
    }

    public boolean isValid(Floor... floors) {
        return Arrays.stream(floors).anyMatch(floor -> floor.compareTo(lowest) >= 0 && floor.compareTo(highest) <= 0);
    }

    public void release(RobotId robotId) {
        //todo 如果机器人无法出梯，且确保安全后，释放电梯，梯控请求是否仍保留
        Request request = this.calledRequests.remove(robotId.getValue());
        if (request != null) {//当机器人主动释放电梯时，表示机器人乘梯失败。如果需要继续乘梯，需重新招唤电梯
            respondFrom(robotId);
        } else {
            log.warn("找不到该机器人【" + robotId + "】搭乘此电梯【" + getId() + "】，系统将忽略该请求");
        }
    }

    private void respondFrom(RobotId robotId) {
        this.notified.remove(robotId);
        if (this.notified.isEmpty()) {
            DomainEventPublisher.publish(new ElevatorDoorReleasedEvent(getId()));
        }
    }

    public void unbind(RobotId robotId) {
        if (!this.whiteList.remove(robotId)) {
            log.warn("机器人【{}】与该电梯【{}】未存在绑定关系", robotId, getId());
        }

    }

    public void bind(RobotId robotId) {
        if (!this.whiteList.add(robotId)) {
            log.warn("机器人【{}】已经绑定到这台电梯【{}】", robotId, getId());
        }
    }

    public void enter(Robot robot) {
        if (!canEnter(robot.getId())) {
            throw new RobotNotAllowedEnterException(robot.getId(), getId());
        }
        Request request = this.calledRequests.remove(robot.getId().getValue());
        if (request == null) {
            log.warn("找不到该机器【{}】乘梯【{}】请求", robot.getId(), getId());
            throw new RequestNotFoundException(robot.getId(), getId());
        }
        this.tookRequests.put(request.getRobotId().getValue(), request);
        robot.enter(getId());
        respondFrom(robot.getId());
    }

    public boolean canEnter(RobotId robotId) {
        return this.whiteList.contains(robotId);
    }

    public void leave(Robot robot) {
        Request request = this.tookRequests.remove(robot.getId().getValue());
        if (request == null) {
            log.warn("找不到该机器【{}】乘梯【{}】请求", robot.getId(), getId());
            throw new RequestNotFoundException(robot.getId(), getId());
        }
        robot.leave(getId());
        DomainEventPublisher.publish(new RobotLeftEvent(getId(), request));
        respondFrom(robot.getId());
    }
}
