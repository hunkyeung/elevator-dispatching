package com.robustel.dispatching.domain.elevator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author YangXuehong
 * @date 2022/4/11
 */
class FloorTest {

    @Test
    void Given_BlankString_When_Of_Then_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Floor.of(0));
    }

}