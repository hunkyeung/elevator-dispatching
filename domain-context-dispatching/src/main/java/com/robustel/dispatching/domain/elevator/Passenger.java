package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.ValueObject;
import lombok.*;

@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class Passenger implements ValueObject {
    private Long id;

    public Passenger(Long id) {
        this.id = id;
    }

    public static Passenger of(@NonNull Long id) {
        return new Passenger(id);
    }
}
