package com.robustel.dispatching.domain.robot;

import com.robustel.dispatching.domain.elevator.Elevator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.yeung.api.AbstractEntity;

import java.time.Instant;

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

    public Robot(RobotId id) {
        super(id);
    }

    public Robot(RobotId id, Instant enteringTime, Instant leavingTime) {
        super(id);
        this.enteringTime = enteringTime;
        this.leavingTime = leavingTime;
    }

    public void leave(Elevator elevator) {
        elevator.leave(getId());
        this.leavingTime = Instant.now();
        log.info("机器人【{}】已经出电梯【{}】", getId(), elevator.getId());
    }

    public void enter(Elevator elevator) {
        elevator.enter(getId());
        reset();
        this.enteringTime = Instant.now();
        log.info("机器人【{}】已经进电梯【{}】", getId(), elevator.getId());
    }

    //reset the entering time and leaving time
    private void reset() {
        log.info("重置机器人【{}】状态：入梯时间和出梯时间", getId());
        this.enteringTime = null;
        this.leavingTime = null;
    }
}
