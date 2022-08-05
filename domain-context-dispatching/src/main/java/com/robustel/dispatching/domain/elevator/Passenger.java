package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
@Getter
@EqualsAndHashCode
public class Passenger implements ValueObject {
    private String id;

    public Passenger(String id) {
        this.id = id;
    }

    public static Passenger of(@NonNull String id) {
        return new Passenger(id);
    }
}
