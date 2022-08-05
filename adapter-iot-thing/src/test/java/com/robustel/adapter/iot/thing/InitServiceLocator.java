package com.robustel.adapter.iot.thing;

import com.google.common.eventbus.EventBus;
import com.robustel.ddd.service.EventPublisher;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.ddd.service.UidGenerator;
import com.robustel.dispatching.domain.elevator.ElevatorController;
import com.robustel.dispatching.domain.elevator.Floor;
import com.robustel.dispatching.domain.elevator.Passenger;
import com.robustel.dispatching.domain.elevator.PassengerController;

import java.util.Map;
import java.util.Random;
import java.util.Set;

public class InitServiceLocator {
    static {
        ServiceLocator.setRegistry(new ServiceLocator.ServiceRegistry() {
            private static Map<Class, Object> services;

            static {
                services = Map.of(
                        UidGenerator.class, (UidGenerator) () -> new Random().nextLong(),
                        EventPublisher.class, new EventPublisher() {
                            private EventBus eventBus = new EventBus();

                            @Override
                            public void register(Object o) {
                                eventBus.register(o);
                            }

                            @Override
                            public void unregister(Object o) {
                                eventBus.unregister(o);
                            }

                            @Override
                            public void publish(Object o) {
                                eventBus.post(o);
                            }
                        },
                        ElevatorController.class, new ElevatorController() {
                            @Override
                            public void press(long elevatorId, Floor floor) {
                                System.out.println("press " + floor);
                            }

                            @Override
                            public void release(long elevatorId) {
                                System.out.println("release " + elevatorId);
                            }

                            @Override
                            public void press(long elevatorId, Set<Floor> pressedFloor) {
                                System.out.println("press " + pressedFloor);
                            }
                        },
                        PassengerController.class, new PassengerController() {
                            @Override
                            public void pleaseOut(Passenger passenger) {
                                System.out.println(passenger + " out.");
                            }

                            @Override
                            public void pleaseIn(Passenger passenger) {
                                System.out.println(passenger + " in.");
                            }
                        });
            }

            @Override
            public <T> T getService(Class<T> aClass) {
                return (T) services.get(aClass);
            }

            @Override
            public <T> T getService(String serviceName, Class<T> aClass) {
                throw new UnsupportedOperationException();
            }
        });
    }

    public static void init() {
        System.out.println("Init...");
    }
}
