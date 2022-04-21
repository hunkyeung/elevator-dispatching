package com.robustel.dispatching.domain.elevator;

import com.robustel.dispatching.domain.robot.RobotId;
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

    public Elevator(ElevatorId id, Floor highest, Floor lowest, State state, Map<String, Request> calledRequests, Map<String, Request> tookRequests,
                    Set<RobotId> whiteList) {
        super(id);
        this.highest = highest;
        this.lowest = lowest;
        this.state = state;
        this.calledRequests = calledRequests;
        this.tookRequests = tookRequests;
        this.whiteList = whiteList;
    }

    public static Elevator of(ElevatorId elevatorId, Floor highest, Floor lowest) {
        return new Elevator(elevatorId, highest, lowest, State.IN_SERVICE, new HashMap<>(), new HashMap<>(), new HashSet<>(0));
    }

    public void response(Request request) {
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

    /**
     * 当请求的机器人进入电梯后，请求变成进行中
     */
    public void enter(RobotId robotId) {
        Request request = this.calledRequests.remove(robotId.getValue());
        if (request == null) {
            log.warn("找不到该机器【{}】乘梯【{}】请求", robotId, getId());
            throw new RequestNotFoundException(robotId, getId());
        } else {
            this.tookRequests.put(request.getRobotId().getValue(), request);
        }
    }

    public void leave(RobotId robotId) {
        Request request = this.tookRequests.remove(robotId.getValue());
        if (request == null) {
            log.warn("找不到该机器【{}】乘梯【{}】请求", robotId, getId());
            throw new RequestNotFoundException(robotId, getId());
        } else {
            DomainEventPublisher.publish(new RobotLeftEvent(getId(), request));
        }
    }

    // 当电梯到达时，通知相关机器人进出电梯
    public void arrive(Floor floor, Direction direction) {
        //先出后进
        noticeRobotToLeave(floor);
        noticeRobotToEnter(floor, direction);
    }

    private void noticeRobotToEnter(Floor floor, Direction direction) {
        //通知机器人进梯
        //todo 后续针对多机一梯，多机多梯时，不能通知所有电梯，而是根据排队通知，如果允许一次多机乘梯时，还需要考虑先进后出，后进进先出原则
        log.info("正在通知机器人进电梯【{}】...", getId());
        Optional.ofNullable(this.calledRequests).orElse(new HashMap<>()).values().forEach(
                request -> {
                    if (request.matchFrom(floor, direction))
                        DomainEventPublisher.publish(new ElevatorArrivedEvent(request.getRobotId(), true));
                }
        );
    }

    private void noticeRobotToLeave(Floor floor) {
        //通知机器人出梯
        log.info("正在通知机器人出电梯【{}】...", getId());
        Optional.ofNullable(this.tookRequests).orElse(new HashMap<>()).values().forEach(
                request -> {
                    if (request.matchTo(floor))
                        DomainEventPublisher.publish(new ElevatorArrivedEvent(request.getRobotId(), false));
                }
        );
    }

    public boolean isValid(Floor... floors) {
        return Arrays.stream(floors).anyMatch(floor -> floor.compareTo(lowest) >= 0 && floor.compareTo(highest) <= 0);
    }

    public void release() {
        //todo 当出现一机多梯时，不能简单释放开门，需要接收到所以通知到的机器人返回释放开门后，方可真正的释放
        DomainEventPublisher.publish(new ElevatorDoorReleasedEvent(getId()));
    }

    public void unbind(RobotId robotId) {
        this.whiteList.remove(robotId);
    }

    public void bind(RobotId robotId) {
        this.whiteList.add(robotId);
    }
}
