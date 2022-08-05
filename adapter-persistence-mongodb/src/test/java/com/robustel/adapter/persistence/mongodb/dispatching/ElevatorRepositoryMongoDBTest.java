package com.robustel.adapter.persistence.mongodb.dispatching;

import com.robustel.adapter.persistence.mongodb.core.MongoPageHelper;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class ElevatorRepositoryMongoDBTest {

    @Test
    void test() {
        var mongoTemplate = mock(MongoTemplate.class);
        ElevatorRepositoryMongoDB elevatorRepositoryMongoDB = new ElevatorRepositoryMongoDB(mongoTemplate, new MongoPageHelper(mongoTemplate));
        assertNotNull(elevatorRepositoryMongoDB);
    }

}