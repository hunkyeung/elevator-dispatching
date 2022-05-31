package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.AbstractEntity;
import com.robustel.ddd.service.EventPublisher;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.ddd.service.UidGenerator;
import com.robustel.dispatching.domain.takingrequesthistory.TakingRequestHistory;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
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
public class Elevator extends AbstractEntity<Long> {
    private String name;
    private Floor highest;//最高楼层
    private Floor lowest;//最低楼层
    private Floor currentFloor;
    private ElevatorState state;
    private Map<Long, TakingRequest> takingRequests;//乘梯请求
    private Set<Passenger> passengers;
    private Set<Passenger> notifiedPassengers;

    public Elevator(Long id, String name, Floor highest, Floor lowest, Floor currentFloor, ElevatorState state, Map<Long, TakingRequest> takingRequests, Set<Passenger> passengers, Set<Passenger> notifiedPassengers) {
        super(id);
        this.name = name;
        this.highest = highest;
        this.lowest = lowest;
        this.currentFloor = currentFloor;
        this.state = state;
        this.takingRequests = takingRequests;
        this.passengers = passengers;
        this.notifiedPassengers = notifiedPassengers;
    }

    public static Elevator create(@NonNull String name, int highest, int lowest, @NonNull String modelId, @NonNull String sn) {
        if (lowest > highest) {
            throw new IllegalArgumentException(String.format("最低楼层【%s】不能大于最高楼层【%s】", lowest, highest));
        }
        Long id = ServiceLocator.service(UidGenerator.class).nextId();
        ServiceLocator.service(EventPublisher.class).publish(new ElevatorRegisteredEvent(id, modelId, sn));
        return new Elevator(id, name, Floor.of(highest), Floor.of(lowest), null, ElevatorState.NONE, new HashMap<>(), new HashSet<>(), new HashSet<>());
    }

    public void tellIn() {
        if (ElevatorState.WAITING_OUT.equals(state)) {
            this.state = ElevatorState.WAITING_IN;
            tell();
            if (this.notifiedPassengers.isEmpty()) {
                log.debug("没有乘客进电梯【{}】...", id());
                ServiceLocator.service(EventPublisher.class).publish(new NoPassengerEvent(id()));
                this.state = ElevatorState.NONE;
            } else {
                log.debug("正在等待乘客进电梯【{}】...", id());
            }
        }
    }

    private void tellOut() {
        if (ElevatorState.NONE.equals(state)) {
            this.state = ElevatorState.WAITING_OUT;
            tell();
            if (this.notifiedPassengers.isEmpty()) {
                log.debug("没有乘客需要出电梯【{}】...", id());
                tellIn();
            } else {
                log.debug("正在等待乘客出电梯【{}】...", id());
            }
        }
    }

    private void tell() {
        Optional.ofNullable(this.takingRequests).orElse(Map.of()).values().forEach(
                takingRequest -> {
                    if (takingRequest.action(state, getCurrentFloor())) {
                        this.notifiedPassengers.add(takingRequest.getPassenger());
                    }
                }
        );
    }

    public boolean isValid(Floor... floors) {
        return Arrays.stream(floors).allMatch(floor -> lowest.compareTo(floor) <= 0 && highest.compareTo(floor) >= 0);
    }

    public TakingRequestHistory cancelTakingRequest(Passenger passenger, String cause) {
        if (!isBinding(passenger)) {
            throw new PassengerNotAllowedException(passenger, id());
        }
        TakingRequest takingRequest = this.takingRequests.remove(passenger.getId());
        if (Objects.isNull(takingRequest)) {
            log.warn("找不到该乘客【{}】乘梯【{}】请求", passenger, id());
            throw new TakingRequestNotFoundException(passenger, id());
        }
        takingRequest.cancel(cause);
        respondFrom(passenger);
        return TakingRequestHistory.create(takingRequest, id());
    }


    public TakingRequestHistory finish(Passenger passenger) {
        if (!isBinding(passenger)) {
            throw new PassengerNotAllowedException(passenger, id());
        }
        if (ElevatorState.NONE.equals(state)) {
            throw new IllegalStateException(String.format("电梯【%s】状态为【%s】，不能接受完成请求", id(), this.state));
        }
        TakingRequest takingRequest = this.takingRequests.get(passenger.getId());
        if (Objects.isNull(takingRequest)) {
            log.warn("找不到该乘客【{}】乘梯【{}】请求", passenger, id());
            throw new TakingRequestNotFoundException(passenger, id());
        }
        takingRequest.finish(this.state);
        respondFrom(passenger);
        if (ElevatorState.WAITING_OUT.equals(this.state)) {
            return TakingRequestHistory.create(this.takingRequests.remove(passenger.getId()), id());
        } else {
            return null;
        }
    }

    public boolean isBinding(Passenger passenger) {
        return this.passengers.contains(passenger);
    }


    private void respondFrom(Passenger passenger) {
        if (this.notifiedPassengers.remove(passenger) && this.notifiedPassengers.isEmpty()) {
            if (ElevatorState.WAITING_OUT.equals(this.state)) {
                ServiceLocator.service(EventPublisher.class).publish(new AllPassengerOutRespondedEvent(id()));
            } else {
                ServiceLocator.service(EventPublisher.class).publish(new AllPassengerInRespondedEvent(id()));
                this.state = ElevatorState.NONE;
            }
        }
    }

    public void unbind(@NonNull Passenger passenger) {
        if (!this.passengers.remove(passenger)) {
            log.warn("该乘客【{}】与该电梯【{}】未存在绑定关系", passenger, id());
        }

    }

    public void bind(@NonNull Passenger passenger) {
        if (!this.passengers.add(passenger)) {
            log.warn("该乘客【{}】已经绑定到这台电梯【{}】", passenger, id());
        }
    }

    public void take(@NonNull Passenger passenger, @NonNull Floor from, @NonNull Floor to) {
        if (this.takingRequests.containsKey(passenger.getId())) {
            throw new TakingRequestAlreadyExistException(passenger, id());
        }
        TakingRequest takingRequest = TakingRequest.create(passenger, from, to);
        this.takingRequests.put(passenger.getId(), takingRequest);
        log.debug("等待调度的乘梯请求:{}", this.takingRequests);
        ServiceLocator.service(EventPublisher.class).publish(new TakingRequestAcceptedEvent(id(), takingRequest));
    }

    public void passengerOutIn() {
        tellOut();
    }

    public void arrive(@NonNull Floor floor) {
        this.currentFloor = floor;
    }

    public void reset() {
        this.state = ElevatorState.NONE;
        this.notifiedPassengers.clear();
    }
}
