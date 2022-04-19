package com.robustel.dispatching.domain.elevator;

import com.robustel.dispatching.domain.robot.RobotId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.yeung.api.ValueObject;

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

    public boolean matchFrom(Floor floor, Direction direction) {
        boolean isMatched = direction().equals(direction) && this.from.equals(floor);
        log.debug("匹配乘梯请求【机器人：{},出发楼层：{},目标楼层：{}】与电梯状态【楼层：{},方向：{}】，结果为{}",
                getRobotId(), getFrom(), getTo(), floor, direction, true);
        return isMatched;
    }

    private Direction direction() {
        Direction direction;
        if (this.from.compareTo(this.to) < 0) {
            direction = Direction.UP;
        } else {
            direction = Direction.DOWN;
        }
        return direction;
    }

    public boolean matchTo(Floor floor) {
        return this.to.equals(floor);
    }
}
