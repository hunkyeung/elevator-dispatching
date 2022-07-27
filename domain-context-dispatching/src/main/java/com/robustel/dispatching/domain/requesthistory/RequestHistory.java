package com.robustel.dispatching.domain.requesthistory;

import com.robustel.ddd.core.AbstractEntity;
import com.robustel.ddd.service.ServiceLocator;
import com.robustel.ddd.service.UidGenerator;
import com.robustel.dispatching.domain.elevator.Request;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author YangXuehong
 * @date 2022/4/14
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RequestHistory extends AbstractEntity<Long> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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
                format(request.getAt()),
                format(request.getIn()),
                format(request.getOut()),
                StringUtils.defaultIfBlank(request.getStatus(), ""),
                elevatorId,
                format(archivedOn));
    }

    private String format(Instant instant) {
        if (Objects.isNull(instant)) {
            return StringUtils.EMPTY;
        } else {
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(DATE_TIME_FORMATTER);
        }
    }

    public record Data(long id, long requestId, String passenger, int from, int to, String at, String in, String out,
                       String status, long elevatorId, String archivedOn) implements Serializable {

    }
}
