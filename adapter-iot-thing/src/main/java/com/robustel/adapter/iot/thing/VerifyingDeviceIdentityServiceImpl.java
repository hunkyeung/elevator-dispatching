package com.robustel.adapter.iot.thing;

import com.robustel.thing.application.VerifyingDeviceIdentityService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class VerifyingDeviceIdentityServiceImpl implements VerifyingDeviceIdentityService {
    @Override
    public boolean isValid(String sn, Map<String, String> otherInfo) {
        return true;
    }
}
