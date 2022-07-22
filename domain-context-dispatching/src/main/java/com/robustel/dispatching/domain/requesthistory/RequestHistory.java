package com.robustel.dispatching.domain.requesthistory;

import com.robustel.ddd.core.AbstractEntity;
import com.robustel.ddd.core.ValueObject;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.ddd.service.UidGenerator;
import com.robustel.dispatching.domain.elevator.Request;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.Optional;

/**
 * @author YangXuehong
 * @date 2022/4/14
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RequestHistory extends AbstractEntity<Long> {
    private Request request;
    private Long elevatorId;
    private Instant archivedOn;

    public RequestHistory(Long id, Request request, Long elevatorId, Instant archivedOn) {
        super(id);
        this.request = request;
        this.elevatorId = elevatorId;
        this.archivedOn = archivedOn;
    }

    public static RequestHistory create(@NonNull Request request, @NonNull Long elevatorId) {
        return new RequestHistory(ServiceLocator.service(UidGenerator.class).nextId(), request, elevatorId, Instant.now());
    }

    public Data toData() {
        return new Data(id(), request.id(), request.getPassenger().getId(), request.getFrom().getValue(), request.getTo().getValue(),
                request.getAt().toEpochMilli(),
                Optional.ofNullable(request.getIn()).orElse(Instant.EPOCH).toEpochMilli(),
                Optional.ofNullable(request.getOut()).orElse(Instant.EPOCH).toEpochMilli(),
                StringUtils.defaultIfBlank(request.getStatus(), ""), elevatorId, archivedOn.toEpochMilli());
    }

    @AllArgsConstructor
    @ToString
    @Getter
    public static class Data implements ValueObject {
        private long id;
        private long requestId;
        private String passenger;
        private int from;
        private int to;
        private long at;
        private long in;
        private long out;
        private String status;
        private long elevatorId;
        private long archivedOn;
    }
}
