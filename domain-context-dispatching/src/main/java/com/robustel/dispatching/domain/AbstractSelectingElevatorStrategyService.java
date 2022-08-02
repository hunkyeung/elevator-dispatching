package com.robustel.dispatching.domain;

import com.robustel.ddd.core.DomainException;
import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.elevator.Passenger;
import lombok.NonNull;

import java.util.Objects;
import java.util.Optional;

public abstract class AbstractSelectingElevatorStrategyService implements SelectingElevatorStrategyService {

    @Override
    public final Optional<Elevator> selectElevator(@NonNull Passenger passenger, @NonNull Floor from, @NonNull Floor to) {
        test(from, to);
        return select(passenger, from, to);
    }

    protected abstract Optional<Elevator> select(Passenger passenger, Floor from, Floor to);

    private void test(Floor from, Floor to) {
        if (Objects.equals(from, to)) {
            throw new InvalidFloorException(String.format("出发楼层【%s】和目标楼层【%s】相同", from, to));
        }
    }

    public static class InvalidFloorException extends DomainException {
        public InvalidFloorException(String message) {
            super(message);
        }
    }
}
