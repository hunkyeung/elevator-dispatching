package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.AbstractEntity;
import com.robustel.ddd.service.EventPublisher;
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
public class TakingRequest extends AbstractEntity<Long> {
    private Passenger passenger;
    private Floor from;
    private Floor to;
    private Instant at;
    private Instant in;
    private Instant out;
    private String status;

    public TakingRequest(Long id, Passenger passenger, Floor from, Floor to, Instant at, Instant in, Instant out, String status) {
        super(id);
        this.passenger = passenger;
        this.from = from;
        this.to = to;
        this.at = at;
        this.in = in;
        this.out = out;
        this.status = status;
    }

    public static TakingRequest create(@NonNull Passenger passenger, @NonNull Floor from, @NonNull Floor to) {
        Long id = ServiceLocator.service(UidGenerator.class).nextId();
        if (from.equals(to)) {
            throw new IllegalArgumentException("出发楼层与目标楼层不能相同");
        }
        return new TakingRequest(id, passenger, from, to, Instant.now(), null, null, null);
    }

    public void cancel(String cause) {
        this.status = cause;
    }

    public void finish(ElevatorState state) {
        if (ElevatorState.WAITING_IN.equals(state) && Objects.isNull(in)) {
            this.in = Instant.now();
        } else if (ElevatorState.WAITING_OUT.equals(state) && !Objects.isNull(in) && Objects.isNull(out)) {
            this.out = Instant.now();
        } else {
            throw new IllegalStateException(String.format("乘梯请求状态不合法【%s】", this));
        }
    }

    public boolean action(ElevatorState state, Floor currentFloor) {
        if (ElevatorState.WAITING_OUT.equals(state) && to.equals(currentFloor) && !Objects.isNull(in)) {
            ServiceLocator.service(EventPublisher.class).publish(new PassengerOutEvent(getPassenger()));
            return true;
        } else if (ElevatorState.WAITING_IN.equals(state) && from.equals(currentFloor) && Objects.isNull(in)) {
            ServiceLocator.service(EventPublisher.class).publish(new PassengerInEvent(getPassenger()));
            return true;
        } else {
            return false;
        }
    }
}
