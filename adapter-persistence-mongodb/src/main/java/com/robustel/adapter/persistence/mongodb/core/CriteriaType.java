package com.robustel.adapter.persistence.mongodb.core;

import org.yeung.api.util.query.Matching;
import org.yeung.api.util.query.Type;

import java.util.Map;

/**
 * @author YangXuehong
 * @date 2022/3/4
 */
public class CriteriaType {
    public static MongoCriteria of(Matching matching, Map<String, String> fieldMap) {
        Type type = matching.getType();
        MongoCriteria mongoCriteria = null;
        switch (type) {
            case eq:
                mongoCriteria = MongoCriteria.ofEq(matching.getField().getValue(fieldMap), matching.getValue());
                break;
            case gt:
                mongoCriteria = MongoCriteria.ofGt(matching.getField().getValue(fieldMap), matching.getValue());
                break;
            case gte:
                mongoCriteria = MongoCriteria.ofGte(matching.getField().getValue(fieldMap), matching.getValue());
                break;
            case lt:
                mongoCriteria = MongoCriteria.ofLt(matching.getField().getValue(fieldMap), matching.getValue());
                break;
            case lte:
                mongoCriteria = MongoCriteria.ofLte(matching.getField().getValue(fieldMap), matching.getValue());
                break;
            case in:
                mongoCriteria = MongoCriteria.ofIn(matching.getField().getValue(fieldMap), matching.getValue());
                break;
            case nin:
                mongoCriteria = MongoCriteria.ofNin(matching.getField().getValue(fieldMap), matching.getValue());
                break;
            case like:
                mongoCriteria = MongoCriteria.ofLike(matching.getField().getValue(fieldMap), matching.getValue());
                break;
            case between:
                mongoCriteria = MongoCriteria.ofBetween(matching.getField().getValue(fieldMap), matching.getValue());
                break;
            default:
                break;
        }
        return mongoCriteria;
    }
}
