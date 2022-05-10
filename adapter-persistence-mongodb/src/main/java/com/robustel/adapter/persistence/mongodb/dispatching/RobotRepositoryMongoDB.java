package com.robustel.adapter.persistence.mongodb.dispatching;

import com.robustel.adapter.persistence.mongodb.core.AbstractRepositoryMongo;
import com.robustel.adapter.persistence.mongodb.core.MongoPageHelper;
import com.robustel.dispatching.domain.robot.Robot;
import com.robustel.dispatching.domain.robot.RobotId;
import com.robustel.dispatching.domain.robot.RobotRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author YangXuehong
 * @date 2022/4/11
 */
@Repository
public class RobotRepositoryMongoDB extends AbstractRepositoryMongo<Robot, RobotId>
        implements RobotRepository {
    protected RobotRepositoryMongoDB(MongoTemplate mongoTemplate, MongoPageHelper mongoPageHelper) {
        super(mongoTemplate, mongoPageHelper);
    }
}
