package com.robustel.adapter.persistence.mongodb.dispatching;

import com.robustel.adapter.persistence.mongodb.core.AbstractRepositoryMongo;
import com.robustel.adapter.persistence.mongodb.core.MongoPageHelper;
import com.robustel.dispatching.domain.elevator.Elevator;
import com.robustel.dispatching.domain.elevator.ElevatorId;
import com.robustel.dispatching.domain.elevator.ElevatorRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author YangXuehong
 * @date 2022/4/11
 */
@Repository
public class ElevatorRepositoryMongoDB extends AbstractRepositoryMongo<Elevator, ElevatorId>
        implements ElevatorRepository {
    protected ElevatorRepositoryMongoDB(MongoTemplate mongoTemplate, MongoPageHelper mongoPageHelper) {
        super(mongoTemplate, mongoPageHelper);
    }
}
