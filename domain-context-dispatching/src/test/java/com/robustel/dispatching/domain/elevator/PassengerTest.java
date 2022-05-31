package com.robustel.dispatching.domain.elevator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PassengerTest {

    @Test
    void Given_Normal_When_Of_Then_GetWhatYouSet() {
        Passenger passenger = Passenger.of(1L);
        assertEquals(1L, passenger.getId());
        assertEquals(Passenger.of(1L), passenger);
    }

    @Test
    void Given_Null_When_Of_Then_ThrowException() {
        assertThrows(NullPointerException.class,
                () -> Passenger.of(null));
    }

}