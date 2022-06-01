package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.AbstractEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PassengerInEvent extends AbstractEvent {
    private Passenger passenger;

    public PassengerInEvent(Passenger passenger) {
        this.passenger = passenger;
    }
}
