package com.robustel.dispatching.domain.elevator;

/**
 * 梯控板
 * 提供电梯按键功能
 */
public interface ElevatorController {
    void press(long elevatorId, Floor floor);

    void release(long elevatorId);
}
