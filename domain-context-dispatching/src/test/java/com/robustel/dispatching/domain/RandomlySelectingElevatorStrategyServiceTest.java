package com.robustel.dispatching.domain;

import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.robot.Robot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author YangXuehong
 * @date 2022/4/22
 */
class RandomlySelectingElevatorStrategyServiceTest {

    private SelectingElevatorStrategyService service;
    private ElevatorRepository elevatorRepository;

    @BeforeEach
    void init() {
        elevatorRepository = mock(ElevatorRepository.class);
        service = new RandomlySelectingElevatorStrategyService(elevatorRepository);
    }

    @Test
    void Given_EmptyList_When_SelectElevator_Then_ThrowsNoElevatorAvailableException() {
        when(elevatorRepository.findByCriteria(any())).thenReturn(new LinkedList<>());
        Assertions.assertThrows(SelectingElevatorStrategyService.NoElevatorAvailableException.class,
                () -> service.selectElevator(mock(Robot.class), Floor.of(-1), Floor.of(10)));
    }

    @Test
    void Given_OneElevator_When_SelectElevator_Then_Return() {
        Elevator elevator = mock(Elevator.class);
        when(elevator.isValid(any(), any())).thenReturn(true);
        when(elevatorRepository.findByCriteria(any())).thenReturn(Arrays.asList(elevator));
        assertEquals(elevator, service.selectElevator(mock(Robot.class), Floor.of(-1), Floor.of(10)));
    }

}