package com.robustel.dispatching.domain.elevator;

public interface PassengerController {
    void pleaseOut(Passenger passenger);

    void pleaseIn(Passenger passenger);
}
