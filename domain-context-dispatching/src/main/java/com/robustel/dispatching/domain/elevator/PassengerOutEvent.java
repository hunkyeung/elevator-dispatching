package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.AbstractEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@NoArgsConstructor
public class PassengerOutEvent extends AbstractEvent {
    private Passenger passenger;

    public PassengerOutEvent(Passenger passenger) {
        this.passenger = passenger;
    }
}
