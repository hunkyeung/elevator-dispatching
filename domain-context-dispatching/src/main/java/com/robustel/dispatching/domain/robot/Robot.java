package com.robustel.dispatching.domain.robot;

import com.robustel.ddd.core.AbstractEntity;
import com.robustel.ddd.service.EventPublisher;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.ddd.service.UidGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author YangXuehong
 * @date 2022/4/8
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class Robot extends AbstractEntity<Long> {
    private String name;

    public Robot(Long id, String name) {
        super(id);
        this.name = name;
    }

    public static Robot create(@NonNull String name, @NonNull String modelId) {
        var id = ServiceLocator.service(UidGenerator.class).nextId();
        ServiceLocator.service(EventPublisher.class).publish(new RobotRegisteredEvent(id, modelId));
        return new Robot(id, name);
    }

}
