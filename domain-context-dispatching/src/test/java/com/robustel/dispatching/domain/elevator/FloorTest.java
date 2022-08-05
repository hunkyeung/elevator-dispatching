package com.robustel.dispatching.domain.elevator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FloorTest {

    @Test
    void Given_Zero_When_Of_Then_ThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class
                , () -> Floor.of(0));
    }

    @Test
    void Given_Negative_When_Of_Then_GetWhatYouSet() {
        Floor negativeFloor = Floor.of(-1);
        assertEquals(Floor.of(-1), negativeFloor);
        assertEquals(-1, negativeFloor.getValue());
    }

    @Test
    void Given_Positive_Then_Of_Then_GetWhatYouSet() {
        Floor positiveFloor = Floor.of(1);
        assertEquals(Floor.of(1), positiveFloor);
        assertEquals(1, positiveFloor.getValue());
    }

    @Test
    void Given_TwoFloor_When_CompareTo_Then_Expected() {
        Floor one = Floor.of(-1);
        Floor two = Floor.of(1);
        assertEquals(-2, one.compareTo(two));
        assertEquals(2, two.compareTo(one));
    }

}