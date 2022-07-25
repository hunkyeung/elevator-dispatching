package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.AbstractEntity;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.ddd.service.UidGenerator;
import com.robustel.dispatching.domain.requesthistory.RequestHistory;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author YangXuehong
 * @date 2022/4/8
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
@Slf4j
public class Elevator extends AbstractEntity<Long> {
    private static final int CAPACITY = 2;
    private String name;
    private Floor highest;//最高楼层
    private Floor lowest;//最低楼层
    private Floor currentFloor;
    private Direction nextDirection;
    private ElevatorState state;
    private Set<Passenger> binding;//乘客绑定电梯
    private Map<String, Request> requests;//乘梯请求
    private List<Passenger> toBeNotified;//待通知乘客列表
    private Passenger notified;//当前通知的乘客
    private List<Passenger> transferPassengers;//中转乘客
    private List<Passenger> onPassage;//乘梯中的乘客
    private Set<Floor> pressedFloor;

    public Elevator(Long id, String name, Floor highest, Floor lowest, Floor currentFloor,
                    Direction nextDirection, ElevatorState state, Map<String, Request> requests,
                    List<Passenger> toBeNotified, Set<Passenger> binding, Passenger notified,
                    List<Passenger> onPassage, List<Passenger> transferPassengers, Set<Floor> pressedFloor) {
        super(id);
        this.name = name;
        this.highest = highest;
        this.lowest = lowest;
        this.currentFloor = currentFloor;
        this.nextDirection = nextDirection;
        this.state = state;
        this.requests = requests;
        this.toBeNotified = toBeNotified;
        this.binding = binding;
        this.notified = notified;
        this.onPassage = onPassage;
        this.transferPassengers = transferPassengers;
        this.pressedFloor = pressedFloor;
    }

    public static Elevator create(@NonNull String name, int highest, int lowest) {
        Long id = ServiceLocator.service(UidGenerator.class).nextId();
        return create(id, name, highest, lowest);
    }

    public static Elevator create(long id, @NonNull String name, int highest, int lowest) {
        if (lowest > highest) {
            throw new IllegalArgumentException(String.format("最低楼层【%s】不能大于最高楼层【%s】", lowest, highest));
        }
        if (id == 0) {
            id = ServiceLocator.service(UidGenerator.class).nextId();
        }
        return new Elevator(id, name, Floor.of(highest), Floor.of(lowest), null,
                Direction.STOP, ElevatorState.NONE, new HashMap<>(), new ArrayList<>(), new HashSet<>(),
                null, new ArrayList<>(), new ArrayList<>(), new HashSet<>());
    }

    public boolean isBinding(Passenger passenger) {
        return this.binding.contains(passenger);
    }

    public void unbind(@NonNull Passenger passenger) {
        if (!this.binding.remove(passenger)) {
            log.warn("该乘客【{}】与该电梯【{}】未存在绑定关系", passenger, id());
        }

    }

    public void bind(@NonNull Passenger passenger) {
        if (!this.binding.add(passenger)) {
            log.warn("该乘客【{}】已经绑定到这台电梯【{}】", passenger, id());
        }
    }

    public void take(@NonNull Passenger passenger, @NonNull Floor from, @NonNull Floor to) {
        if (this.requests.containsKey(passenger.getId())) {
            throw new RequestAlreadyExistException(passenger, id());
        }
        Request request = Request.create(passenger, from, to);
        this.requests.put(passenger.getId(), request);
        if (this.pressedFloor.add(from)) {
            ServiceLocator.service(ElevatorController.class).press(id(), from);
        } else {
            log.debug("存在相同楼层的请求，忽略楼层【{}】指令", from);
        }
    }

    public void arrive(@NonNull Floor floor, @NonNull Direction nextDirection) {
        this.currentFloor = floor;
        this.nextDirection = nextDirection;
        this.pressedFloor.remove(floor);
        notifyPassengerOut();
    }

    public boolean isMatched(Floor from, Floor to) {
        return from.compareTo(lowest) >= 0 && from.compareTo(highest) <= 0 && to.compareTo(lowest) >= 0 && to.compareTo(highest) <= 0;
    }

    public void release() {
        this.state = ElevatorState.NONE;
        this.notified = null;
        this.currentFloor = null;
        this.nextDirection = null;
        this.toBeNotified.clear();
        ServiceLocator.service(ElevatorController.class).release(id());
    }

    public void notifyPassengerIn() {
        this.state = ElevatorState.WAITING_IN;
        prepareToBeNotifiedIn();
        notifyNext();
    }

    private void prepareToBeNotifiedIn() {
        List<Passenger> toBeTook = this.requests.values().stream()
                .filter(request -> request.shouldIn(currentFloor, nextDirection))
                .sorted(Comparator.comparing(Request::getAt).reversed()).map(Request::getPassenger).toList();
        int fromIndex = 0;
        int toIndex = Math.min(toBeTook.size(), CAPACITY);
        this.transferPassengers.addAll(toBeTook.subList(fromIndex, toIndex));
        this.toBeNotified = this.requests.values().stream()
                .filter(request -> transferPassengers.contains(request.getPassenger()))
                .sorted(Comparator.comparing(Request::getTo)).map(Request::getPassenger).collect(Collectors.toList());
        //todo 由于目前无法获取电梯下一时刻运行方向，故只要匹配出发楼层就符合入梯条件。此时先进后进缺少了判断依据，对于一梯多机调度效率会有影响
        /**
         if (Direction.DOWN.equals(this.direction)) {
         this.toBeNotified = this.requests.values().stream()
         .filter(request -> transferStation.contains(request.getPassenger()))
         .sorted(Comparator.comparing(Request::getTo)).map(Request::getPassenger).collect(Collectors.toList());
         } else {
         this.toBeNotified = this.requests.values().stream()
         .filter(request -> transferStation.contains(request.getPassenger()))
         .sorted(Comparator.comparing(Request::getTo).reversed()).map(Request::getPassenger).collect(Collectors.toList());
         }
         **/
        this.transferPassengers.clear();
        log.debug(String.format("准备待通知进梯乘客列表【%s】...", this.toBeNotified));
    }

    private void notifyNextIn() {
        if (this.toBeNotified.isEmpty()) {
            this.state = ElevatorState.COMPLETED_IN;
            ServiceLocator.service(ElevatorController.class).release(id());
        } else {
            this.notified = this.toBeNotified.remove(0);
            ServiceLocator.service(PassengerController.class).pleaseIn(this.notified);
        }
    }

    public void notifyPassengerOut() {
        this.state = ElevatorState.WAITING_OUT;
        prepareToBeNotifiedOut();
        notifyNext();
    }

    private void prepareToBeNotifiedOut() {
        this.toBeNotified = this.requests.values().stream()
                .filter(request -> request.shouldOut(currentFloor))
                .map(Request::getPassenger)
                .collect(Collectors.toList());
    }

    private void notifyNextOut() {
        if (toBeNotified.isEmpty()) {
            this.state = ElevatorState.COMPLETED_OUT;
            notifyPassengerIn();
        } else {
            this.notified = new OnPassageStack().pop();
            ServiceLocator.service(PassengerController.class).pleaseOut(this.notified);
            if (!toBeNotified.remove(this.notified)) {
                this.transferPassengers.add(this.notified);
            }
        }
    }

    public RequestHistory finish(Passenger passenger) {
        if (Objects.isNull(notified) || !notified.equals(passenger)) {
            throw new IllegalStateException(String.format("未通知该乘客【%s】出进梯", passenger.getId()));
        }
        this.notified = null;
        Request request = this.requests.get(passenger.getId());
        request.finish(this.state);
        RequestHistory history = null;
        if (ElevatorState.WAITING_OUT.equals(state)) {
            history = RequestHistory.create(this.requests.remove(passenger.getId()), id());
        } else if (ElevatorState.WAITING_IN.equals(state)) {
            new OnPassageStack().push(request.getPassenger());
            if (this.pressedFloor.add(request.getTo())) {
                ServiceLocator.service(ElevatorController.class).press(id(), request.getTo());
            }
        }
        notifyNext();
        return history;
    }

    public RequestHistory cancelRequest(Passenger passenger, String cause) {
        Request request = this.requests.remove(passenger.getId());
        if (Objects.isNull(request)) {
            log.warn("找不到该乘客【{}】乘梯【{}】请求", passenger, id());
            throw new RequestNotFoundException(passenger, id());
        }
        request.cancel(cause);
        this.notified = null;
        notifyNext();
        return RequestHistory.create(request, id());
    }

    private void notifyNext() {
        if (ElevatorState.WAITING_OUT.equals(this.state)) {
            notifyNextOut();
        } else if (ElevatorState.WAITING_IN.equals(this.state)) {
            notifyNextIn();
        }
    }

    private class OnPassageStack {
        public Passenger pop() {
            return onPassage.remove(onPassage.size() - 1);
        }

        public void push(Passenger passenger) {
            onPassage.add(passenger);
        }
    }
}
