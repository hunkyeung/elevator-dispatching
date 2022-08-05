package com.robustel.dispatching.application;

import com.robustel.ddd.query.Page;
import com.robustel.ddd.query.PageResult;
import com.robustel.ddd.query.Query;
import com.robustel.ddd.query.Type;
import com.robustel.dispatching.domain.requesthistory.RequestHistoryRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class GettingRequestHistoryApplicationTest {

    @Test
    void test() {
        RequestHistoryRepository repository = mock(RequestHistoryRepository.class);
        when(repository.findByCriteria(any(), any())).thenReturn((new PageResult<>(1, 10, 0L, 0, List.of())));
        GettingRequestHistoryApplication gettingRequestHistoryApplication = new GettingRequestHistoryApplication(repository);
        gettingRequestHistoryApplication.getRequestHistory(1L, "1", Page.of(10, 20));
        Query query = new Query.Builder().matching(Type.EQ, "elevatorId", 1L)
                .matching(Type.EQ, "request.passenger._id", "1").build();
        verify(repository).findByCriteria(query, Page.of(10, 20));
    }

    @Test
    void testElevatorNull() {
        RequestHistoryRepository repository = mock(RequestHistoryRepository.class);
        when(repository.findByCriteria(any(), any())).thenReturn((new PageResult<>(1, 10, 0L, 0, List.of())));
        GettingRequestHistoryApplication gettingRequestHistoryApplication = new GettingRequestHistoryApplication(repository);
        gettingRequestHistoryApplication.getRequestHistory(null, "1", Page.of(10, 20));
        Query query = new Query.Builder()
                .matching(Type.EQ, "request.passenger._id", "1").build();
        verify(repository).findByCriteria(query, Page.of(10, 20));
    }

    @Test
    void testPassengerIdNull() {
        RequestHistoryRepository repository = mock(RequestHistoryRepository.class);
        when(repository.findByCriteria(any(), any())).thenReturn((new PageResult<>(1, 10, 0L, 0, List.of())));
        GettingRequestHistoryApplication gettingRequestHistoryApplication = new GettingRequestHistoryApplication(repository);
        gettingRequestHistoryApplication.getRequestHistory(1L, null, Page.of(10, 20));
        Query query = new Query.Builder().matching(Type.EQ, "elevatorId", 1L)
                .build();
        verify(repository).findByCriteria(query, Page.of(10, 20));
    }

    @Test
    void testAllNull() {
        RequestHistoryRepository repository = mock(RequestHistoryRepository.class);
        when(repository.findByCriteria(any(), any())).thenReturn((new PageResult<>(1, 10, 0L, 0, List.of())));
        GettingRequestHistoryApplication gettingRequestHistoryApplication = new GettingRequestHistoryApplication(repository);
        gettingRequestHistoryApplication.getRequestHistory(null, null, Page.of(10, 20));
        Query query = new Query.Builder().build();
        verify(repository).findByCriteria(query, Page.of(10, 20));
    }

}