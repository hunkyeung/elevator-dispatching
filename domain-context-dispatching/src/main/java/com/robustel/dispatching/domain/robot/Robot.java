package com.robustel.dispatching.domain.robot;

import com.robustel.ddd.core.AbstractEntity;
import com.robustel.ddd.core.DomainException;
import com.robustel.ddd.service.EventPublisher;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.ddd.service.UidGenerator;
import lombok.*;

/**
 * @author YangXuehong
 * @date 2022/4/8
 */
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Robot extends AbstractEntity<Long> {
    private String name;

    public Robot(Long id, String name) {
        super(id);
        this.name = name;
    }

    public static Robot create(@NonNull String name, @NonNull String modelId) {
        long id = ServiceLocator.service(UidGenerator.class).nextId();
        ServiceLocator.service(EventPublisher.class).publish(new RobotRegisteredEvent(id, modelId));
        return new Robot(id, name);
    }

    /**
     * @author YangXuehong
     * @date 2022/4/19
     */
    @ToString(callSuper = true)
    public static class RobotNotFoundException extends DomainException {
        public RobotNotFoundException(Long robotId) {
            super(String.format("找不到该机器人【%s】", robotId));
        }
    }
}
