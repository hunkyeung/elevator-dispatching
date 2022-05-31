package com.robustel.dispatching.domain;

import com.google.common.eventbus.EventBus;
import com.robustel.ddd.service.EventPublisher;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.ddd.service.UidGenerator;

import java.util.Map;
import java.util.Random;

public class InitServiceLocator {
    static {
        ServiceLocator.setRegistry(new ServiceLocator.ServiceRegistry() {
            private static Map<Class, Object> services;

            static {
                services = Map.of(UidGenerator.class, (UidGenerator) () -> new Random().nextLong(),
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
                        });
            }

            @Override
            public <T> T getService(Class<T> aClass) {
                return (T) services.get(aClass);
            }

            @Override
            public <T> T getBean(String s, Class<T> aClass) {
                throw new UnsupportedOperationException();
            }
        });
    }

    public static void init() {
        System.out.println("Init...");
    }
}
