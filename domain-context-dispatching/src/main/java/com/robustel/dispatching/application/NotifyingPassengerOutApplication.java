package com.robustel.dispatching.application;

import com.google.common.eventbus.Subscribe;
import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorDoorOpenedEvent;
import org.springframework.stereotype.Service;

@Service
public class NotifyingPassengerOutApplication {

    public void doNotifyPassengerOut(Elevator elevator) {
        elevator.notifyPassengerOut();
    }

    @Subscribe
    public void listenOn(ElevatorDoorOpenedEvent event) {
        doNotifyPassengerOut(event.getElevator());
    }
}
