package com.robustel.dispatching.domain.robot;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.yeung.api.AbstractEntity;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * @author YangXuehong
 * @date 2022/4/8
 */
@Getter
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Robot extends AbstractEntity<RobotId> {
    private Instant enteringTime;
    private Instant leavingTime;
    private Set<ElevatorId> whiteList;

    public Robot(RobotId id) {
        this(id, null, null, new HashSet<>());
    }

    public Robot(RobotId id, Instant enteringTime, Instant leavingTime, Set<ElevatorId> whiteList) {
        super(id);
        this.enteringTime = enteringTime;
        this.leavingTime = leavingTime;
        this.whiteList = whiteList;
    }

    public void enter(ElevatorId elevatorId) {
        reset();
        this.enteringTime = Instant.now();
        log.debug("机器人【{}】已经进电梯【{}】", getId(), elevatorId);
    }

    public void leave(ElevatorId elevatorId) {
        if (this.enteringTime == null) {
            throw new RobotNotEnterElevatorException(getId(), elevatorId);
        }
        this.leavingTime = Instant.now();
        log.debug("机器人【{}】已经出电梯【{}】", getId(), elevatorId);
    }

    //reset the entering time and leaving time
    private void reset() {
        log.debug("重置机器人【{}】状态：入梯时间和出梯时间", getId());
        this.enteringTime = null;
        this.leavingTime = null;
    }

    public void bind(Elevator elevator) {
        elevator.bind(getId());
        this.whiteList.add(elevator.getId());
    }

    public void unbind(Elevator elevator) {
        elevator.unbind(getId());
        this.whiteList.remove(elevator.getId());
    }

}
