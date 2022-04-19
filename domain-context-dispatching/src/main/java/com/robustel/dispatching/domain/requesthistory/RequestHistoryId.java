package com.robustel.dispatching.domain.requesthistory;

import org.yeung.api.AbstractIdentity;

/**
 * @author YangXuehong
 * @date 2022/4/14
 */
public class RequestHistoryId extends AbstractIdentity<Long> {
    protected RequestHistoryId(Long value) {
        super(value);
    }

    public static RequestHistoryId of(Long value) {
        return new RequestHistoryId(value);
    }
}
