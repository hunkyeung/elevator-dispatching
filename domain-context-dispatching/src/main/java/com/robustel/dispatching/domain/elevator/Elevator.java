package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.AbstractEntity;
import com.robustel.ddd.core.DomainException;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.ddd.service.UidGenerator;
import com.robustel.dispatching.domain.requesthistory.RequestHistory;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author YangXuehong
 * @date 2022/4/8
 */
@Getter
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class Elevator extends AbstractEntity<Long> {
    private static final int CAPACITY = 2;
    private final String name;
    private final Floor highest;//最高楼层
    private final Floor lowest;//最低楼层
    private Floor currentFloor;
    private Direction nextDirection;
    private ElevatorState state;
    private StateMode stateMode;
    private final Set<Passenger> binding;//乘客绑定电梯
    private final Map<String, Request> requests;//乘梯请求
    private List<Passenger> toBeNotified;//待通知乘客列表
    private Passenger notified;//当前通知的乘客
    private List<Passenger> transferPassengers;//中转乘客
    private final List<Passenger> onPassage;//乘梯中的乘客
    private final Set<Floor> pressedFloor;

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
        this.stateMode = initStateMode(state);
        this.requests = requests;
        this.toBeNotified = toBeNotified;
        this.binding = binding;
        this.notified = notified;
        this.onPassage = onPassage;
        this.transferPassengers = transferPassengers;
        this.pressedFloor = pressedFloor;
    }

    private StateMode initStateMode(ElevatorState state) {
        return switch (state) {
            case WAITING_OUT -> new WaitingOutStateMode();
            case WAITING_IN -> new WaitingInStateMode();
            default -> new NoneStateMode();
        };
    }

    public static Elevator create(@NonNull String name, int highest, int lowest) {
        return create(ServiceLocator.service(UidGenerator.class).nextId(), name, highest, lowest);
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
        pressFloor(from);
    }

    public void arrive(@NonNull Floor floor, @NonNull Direction nextDirection) {
        this.currentFloor = floor;
        this.nextDirection = nextDirection;
        this.pressedFloor.remove(floor);
        this.stateMode = new WaitingOutStateMode();
        this.stateMode.prepare();
    }

    public Optional<RequestHistory> finish(Passenger passenger) {
        return stateMode.finish(passenger);
    }

    public RequestHistory cancelRequest(Passenger passenger, String cause) {
        return stateMode.cancelRequest(passenger, cause);
    }

    public boolean isMatched(Floor from, Floor to) {
        return from.compareTo(lowest) >= 0 && from.compareTo(highest) <= 0 && to.compareTo(lowest) >= 0 && to.compareTo(highest) <= 0;
    }

    public void release() {
        this.stateMode = new NoneStateMode();
        this.notified = null;
        this.currentFloor = null;
        this.nextDirection = null;
        this.toBeNotified.clear();
        ServiceLocator.service(ElevatorController.class).release(id());
    }

    private void pressFloor(Floor floor) {
        stateMode.press(floor);
    }

    private class OnPassageStack {
        public void pop() {
            onPassage.remove(onPassage.size() - 1);
        }

        public Passenger peak() {
            return onPassage.get(onPassage.size() - 1);
        }

        public void push(Passenger passenger) {
            onPassage.add(passenger);
        }
    }

    private interface StateMode {
        void prepare();

        RequestHistory cancelRequest(Passenger passenger, String cause);

        Optional<RequestHistory> finish(Passenger passenger);

        void press(Floor floor);
    }

    private abstract class AbstractStateMode implements StateMode {

        @Override
        public void prepare() {
        }

        protected void process(Passenger passenger) {

        }

        @Override
        public final RequestHistory cancelRequest(Passenger passenger, String cause) {
            Request request = requests.remove(passenger.getId());
            if (Objects.isNull(request)) {
                log.warn("找不到该乘客【{}】乘梯【{}】请求", passenger, id());
                throw new RequestNotFoundException(passenger, id());
            }
            request.cancel(cause);
            onPassage.remove(passenger);
            process(passenger);
            return RequestHistory.create(request, id());
        }

        @Override
        public Optional<RequestHistory> finish(Passenger passenger) {
            throw new RequestFinishedNotAllowedException(String.format("电梯当前状态为【%s】，不允许执行完成【finish】动作", state));
        }
    }


    public class NoneStateMode extends AbstractStateMode {
        public NoneStateMode() {
            state = ElevatorState.NONE;
        }

        @Override
        public void press(Floor floor) {
            if (pressedFloor.add(floor)) {
                ServiceLocator.service(ElevatorController.class).press(id(), floor);
            }
        }
    }

    public abstract class AbstractNotNoneStateMode extends AbstractStateMode {
        protected abstract void next();

        @Override
        protected void process(Passenger passenger) {
            if (notified.equals(passenger)) {
                next();
            } else {
                toBeNotified.remove(passenger);
            }
        }

        @Override
        public void press(Floor floor) {
            pressedFloor.add(floor);
        }
    }

    private class WaitingOutStateMode extends AbstractNotNoneStateMode {
        public WaitingOutStateMode() {
            state = ElevatorState.WAITING_OUT;
        }

        @Override
        public void prepare() {
            toBeNotified = requests.values().stream()
                    .filter(request -> request.shouldOut(currentFloor))
                    .map(Request::getPassenger)
                    .collect(Collectors.toList());
            log.debug(String.format("准备待通知出梯乘客列表【%s】...", toBeNotified));
            next();
        }

        @Override
        protected void next() {
            if (toBeNotified.isEmpty()) {
                stateMode = new WaitingInStateMode();
                stateMode.prepare();
            } else {
                notified = new OnPassageStack().peak();
                ServiceLocator.service(PassengerController.class).pleaseOut(notified);
                if (!toBeNotified.remove(notified)) {
                    transferPassengers.add(notified);
                }
            }
        }

        @Override
        public Optional<RequestHistory> finish(Passenger passenger) {
            if (Objects.isNull(notified) || !notified.equals(passenger)) {
                throw new RequestFinishedNotAllowedException(String.format("未通知该乘客【%s】出进梯，不允许执行完成【finish】动作", passenger.getId()));
            }
            Request request = requests.remove(passenger.getId());
            request.finishOut();
            RequestHistory requestHistory = RequestHistory.create(request, id());
            new OnPassageStack().pop();
            next();
            return Optional.of(requestHistory);
        }

    }

    private class WaitingInStateMode extends AbstractNotNoneStateMode {

        public WaitingInStateMode() {
            state = ElevatorState.WAITING_IN;
        }

        @Override
        public void prepare() {
            List<Passenger> toBeTook = requests.values().stream()
                    .filter(request -> request.shouldIn(currentFloor, nextDirection))
                    .sorted(Comparator.comparing(Request::getAt).reversed()).map(Request::getPassenger).toList();
            int fromIndex = 0;
            int toIndex = Math.min(toBeTook.size(), CAPACITY);
            transferPassengers.addAll(toBeTook.subList(fromIndex, toIndex));
            toBeNotified = requests.values().stream()
                    .filter(request -> transferPassengers.contains(request.getPassenger()))
                    .sorted(Comparator.comparing(Request::getTo)).map(Request::getPassenger).collect(Collectors.toList());
            transferPassengers.clear();
            log.debug(String.format("准备待通知进梯乘客列表【%s】...", toBeNotified));
            next();
        }

        @Override
        protected void next() {
            if (toBeNotified.isEmpty()) {
                ServiceLocator.service(ElevatorController.class).press(id(), pressedFloor);
                release();
            } else {
                notified = toBeNotified.remove(0);
                ServiceLocator.service(PassengerController.class).pleaseIn(notified);
            }
        }

        @Override
        public Optional<RequestHistory> finish(Passenger passenger) {
            if (Objects.isNull(notified) || !notified.equals(passenger)) {
                throw new RequestFinishedNotAllowedException(String.format("未通知该乘客【%s】出进梯，不允许执行完成【finish】动作", passenger.getId()));
            }
            Request request = requests.get(passenger.getId());
            request.finishIn();
            new OnPassageStack().push(passenger);
            pressFloor(request.getTo());
            next();
            return Optional.empty();
        }
    }

    /**
     * @author YangXuehong
     * @date 2022/4/19
     */
    @ToString(callSuper = true)
    public static class ElevatorNotFoundException extends DomainException {
        public ElevatorNotFoundException(Long elevatorId) {
            super(String.format("找不到该电梯【%s】", elevatorId));
        }
    }

    /**
     * @author YangXuehong
     * @date 2022/4/19
     */
    @ToString(callSuper = true)
    public static class RequestNotFoundException extends DomainException {
        public RequestNotFoundException(Passenger passenger, Long elevatorId) {
            super(String.format("找不到该乘客【%s】乘梯【%s】请求", passenger, elevatorId));
        }
    }

    /**
     * @author YangXuehong
     * @date 2022/4/19
     */
    @ToString(callSuper = true)
    public static class RequestAlreadyExistException extends DomainException {
        public RequestAlreadyExistException(Passenger passenger, Long elevatorId) {
            super(String.format("已经存在该机器人【%s】搭乘电梯【%s】请求", passenger, elevatorId));
        }
    }

    @ToString(callSuper = true)
    public static class RequestFinishedNotAllowedException extends DomainException {
        public RequestFinishedNotAllowedException(String message) {
            super(message);
        }
    }
}
