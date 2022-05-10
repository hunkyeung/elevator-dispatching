package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.ValueObject;
import com.robustel.dispatching.domain.robot.RobotId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

/**
 * @author YangXuehong
 * @date 2022/4/8
 */
@Slf4j
@Getter
@NoArgsConstructor
@ToString(callSuper = true)
public class Request implements ValueObject {
    private RobotId robotId;
    private Instant occurredOn;
    private Floor from;
    private Floor to;

    public Request(RobotId robotId, Instant occurredOn, Floor from, Floor to) {
        this.robotId = robotId;
        this.occurredOn = occurredOn;
        this.from = from;
        this.to = to;
    }

    public static Request of(RobotId robotId, Floor from, Floor to) {
        if (from == null) {
            throw new IllegalArgumentException("出发楼层不允许为空");
        }
        if (to == null) {
            throw new IllegalArgumentException("目标楼层不允许为空");
        }
        if (from.equals(to)) {
            throw new IllegalArgumentException("出发楼层与目标楼层不能相同");
        }
        return new Request(robotId, Instant.now(), from, to);
    }

    public boolean matchFrom(Floor floor) {
        return this.from.equals(floor);
    }

    public boolean matchTo(Floor floor) {
        return this.to.equals(floor);
    }
}
