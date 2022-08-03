package com.robustel.adapter.iot.thing;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class VerifyingDeviceIdentityServiceImplTest {

    @Test
    void test() {
        var service = new VerifyingDeviceIdentityServiceImpl();
        assertTrue(service.isValid("", Map.of()));
    }

}