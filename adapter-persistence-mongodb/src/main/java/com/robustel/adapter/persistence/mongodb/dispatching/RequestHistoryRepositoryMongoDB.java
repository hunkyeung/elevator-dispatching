package com.robustel.adapter.persistence.mongodb.dispatching;

import com.robustel.adapter.persistence.mongodb.core.AbstractRepositoryMongo;
import com.robustel.adapter.persistence.mongodb.core.MongoPageHelper;
import com.robustel.dispatching.domain.requesthistory.RequestHistory;
import com.robustel.dispatching.domain.requesthistory.RequestHistoryId;
import com.robustel.dispatching.domain.requesthistory.RequestHistoryRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author YangXuehong
 * @date 2022/4/14
 */
@Repository
public class RequestHistoryRepositoryMongoDB extends AbstractRepositoryMongo<RequestHistory, RequestHistoryId>
        implements RequestHistoryRepository {

    protected RequestHistoryRepositoryMongoDB(MongoTemplate mongoTemplate, MongoPageHelper mongoPageHelper) {
        super(mongoTemplate, mongoPageHelper);
    }
}
