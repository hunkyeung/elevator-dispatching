package com.robustel.dispatching.domain.elevator;

import com.robustel.ddd.core.DomainException;
import lombok.ToString;

/**
 * @author YangXuehong
 * @date 2022/4/19
 */
@ToString(callSuper = true)
public class RequestAlreadyExistException extends DomainException {
    public RequestAlreadyExistException(Passenger passenger, Long elevatorId) {
        super(String.format("已经存在该机器人【%s】搭乘电梯【%s】请求", passenger, elevatorId));
    }
}
