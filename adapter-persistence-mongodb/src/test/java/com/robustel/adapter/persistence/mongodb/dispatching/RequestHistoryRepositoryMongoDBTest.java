package com.robustel.adapter.persistence.mongodb.dispatching;

import com.robustel.adapter.persistence.mongodb.core.MongoPageHelper;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.mockito.Mockito.mock;

class RequestHistoryRepositoryMongoDBTest {

    @Test
    void test() {
        var mongoTemplate = mock(MongoTemplate.class);
        new RequestHistoryRepositoryMongoDB(mongoTemplate, new MongoPageHelper(mongoTemplate));
    }

}