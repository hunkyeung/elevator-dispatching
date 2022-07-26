package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.robot.Robot;
import com.robustel.dispatching.domain.robot.RobotRepository;
import lombok.Data;
import lombok.ToString;
import org.springframework.stereotype.Service;

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

    public Long doRegister(Command command) {
        Robot robot = Robot.create(command.getName(), command.getModelId());
        robotRepository.save(robot);
        return robot.id();
    }

    @Data
    @ToString
    public static class Command {
        private String name;
        private String modelId;
    }
}
