package com.robustel.dispatching.application;

import com.robustel.dispatching.domain.robot.Robot;
import com.robustel.dispatching.domain.robot.RobotRepository;
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
        var robot = Robot.create(command.name, command.modelId);
        robotRepository.save(robot);
        return robot.id();
    }

    public record Command(String name, String modelId) {

    }
}
