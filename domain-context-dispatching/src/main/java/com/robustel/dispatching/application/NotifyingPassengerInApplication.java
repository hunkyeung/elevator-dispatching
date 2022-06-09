package com.robustel.dispatching.application;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorCompletedOutEvent;
import org.springframework.stereotype.Service;

@Service
public class NotifyingPassengerInApplication {
    public void doNotifyPassengerIn(Elevator elevator) {
        elevator.notifyPassengerIn();
    }

    @Subscribe
    public void listenOn(ElevatorCompletedOutEvent event) {
        doNotifyPassengerIn(event.getElevator());
    }
}
