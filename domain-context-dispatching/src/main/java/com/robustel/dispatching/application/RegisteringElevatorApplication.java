package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.elevator.*;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.yeung.api.util.DomainEventPublisher;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author YangXuehong
 * @date 2022/4/11
 */
@Service
public class RegisteringElevatorApplication {
    private final ElevatorRepository elevatorRepository;

    public RegisteringElevatorApplication(ElevatorRepository elevatorRepository) {
        this.elevatorRepository = elevatorRepository;
    }

    public ElevatorId doRegister(Command command) {
        ElevatorId elevatorId;
        if (StringUtils.isBlank(command.getElevatorId())) {
            elevatorId = ElevatorId.of(UUID.randomUUID().toString());
        } else {
            elevatorId = ElevatorId.of(command.getElevatorId());
        }
        Elevator elevator = Elevator.of(elevatorId, Floor.of(command.getHighest()), Floor.of(command.getLowest()));
        elevatorRepository.save(elevator);
        Map<String, Serializable> params = new HashMap<>();
        params.put("modelId", command.getModelId());
        params.put("sn", command.getSn());
        DomainEventPublisher.publish(new ElevatorRegisteredEvent(elevatorId, params));
        return elevator.getId();
    }

    @Getter
    public static class Command {
        private String elevatorId;
        private int highest;
        private int lowest;
        private String modelId;
        private String sn;
    }
}
