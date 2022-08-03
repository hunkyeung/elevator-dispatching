package com.robustel.adapter.persistence.mongodb.dispatching;

import com.robustel.adapter.persistence.mongodb.core.MongoPageHelper;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.mockito.Mockito.mock;

class RobotRepositoryMongoDBTest {
    @Test
    void test() {
        var mongoTemplate = mock(MongoTemplate.class);
        new RobotRepositoryMongoDB(mongoTemplate, new MongoPageHelper(mongoTemplate));
    }
}