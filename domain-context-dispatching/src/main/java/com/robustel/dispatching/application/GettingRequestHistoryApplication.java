package com.robustel.dispatching.application;

import com.robustel.ddd.query.Page;
import com.robustel.ddd.query.PageResult;
import com.robustel.ddd.query.Query;
import com.robustel.ddd.query.Type;
import com.robustel.dispatching.domain.requesthistory.RequestHistory;
import com.robustel.dispatching.domain.requesthistory.RequestHistoryRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GettingRequestHistoryApplication {
    public static final String ELEVATOR_ID = "elevatorId";
    public static final String REQUEST_PASSENGER_ID = "request.passenger._id";
    private final RequestHistoryRepository repository;

    public GettingRequestHistoryApplication(RequestHistoryRepository repository) {
        this.repository = repository;
    }

    public PageResult<RequestHistory.Data> getRequestHistory(Long elevatorId, String passenger, Page page) {
        Query.Builder builder = new Query.Builder();
        if (Objects.nonNull(elevatorId)) {
            builder.matching(Type.EQ, ELEVATOR_ID, elevatorId);
        }
        if (!StringUtils.isBlank(passenger)) {
            builder.matching(Type.EQ, REQUEST_PASSENGER_ID, passenger);
        }
        PageResult<RequestHistory> requestHistoryPageResult = repository.findByCriteria(builder.build(), page);
        List<RequestHistory.Data> requestHistoryDataList = requestHistoryPageResult.getRows().stream().map(requestHistory -> requestHistory.toData()).collect(Collectors.toList());
        return requestHistoryPageResult.of(requestHistoryDataList);
    }

}
