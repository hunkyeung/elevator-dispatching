package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.robot.Robot;
import com.robustel.dispatching.domain.robot.RobotId;
import com.robustel.dispatching.domain.robot.RobotRegisteredEvent;
import com.robustel.dispatching.domain.robot.RobotRepository;
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
public class RegisteringRobotApplication {
    private final RobotRepository robotRepository;

    public RegisteringRobotApplication(RobotRepository robotRepository) {
        this.robotRepository = robotRepository;
    }

    public String doRegister(Command command) {
        RobotId robotId;
        if (StringUtils.isBlank(command.getRobotId())) {
            robotId = RobotId.of(UUID.randomUUID().toString());
        } else {
            robotId = RobotId.of(command.getRobotId());
        }
        Robot robot = new Robot(robotId);
        robotRepository.save(robot);
        Map<String, Serializable> params = new HashMap<>();
        params.put("modelId", command.getModelId());
        DomainEventPublisher.publish(new RobotRegisteredEvent(robotId, params));
        return robot.getId().getValue();
    }

    @Getter
    public static class Command {
        private String robotId;
        private String modelId;
    }
}
