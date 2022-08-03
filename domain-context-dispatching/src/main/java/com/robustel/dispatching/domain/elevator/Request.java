package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.AbstractEntity;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.ddd.service.UidGenerator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Objects;

/**
 * @author YangXuehong
 * @date 2022/4/8
 */
@Slf4j
@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Request extends AbstractEntity<Long> {
    private Passenger passenger;
    private Floor from;
    private Floor to;
    private Instant at;
    private Instant in;
    private Instant out;
    private String status;

    public Request(Long id, Passenger passenger, Floor from, Floor to, Instant at, Instant in, Instant out, String status) {
        super(id);
        this.passenger = passenger;
        this.from = from;
        this.to = to;
        this.at = at;
        this.in = in;
        this.out = out;
        this.status = status;
    }

    public static Request create(@NonNull Passenger passenger, @NonNull Floor from, @NonNull Floor to) {
        var id = ServiceLocator.service(UidGenerator.class).nextId();
        if (from.equals(to)) {
            throw new IllegalArgumentException("出发楼层与目标楼层不能相同");
        }
        return new Request(id, passenger, from, to, Instant.now(), null, null, null);
    }

    public void cancel(String cause) {
        this.status = cause;
    }

    public boolean shouldIn(Floor floor, Direction direction) {
        log.debug("{}", direction);
        return this.from.equals(floor);
    }

    public boolean shouldOut(Floor floor) {
        return this.to.equals(floor) && !Objects.isNull(this.in);
    }

    public void finishOut() {
        this.out = Instant.now();
    }

    public void finishIn() {
        this.in = Instant.now();
    }
}
