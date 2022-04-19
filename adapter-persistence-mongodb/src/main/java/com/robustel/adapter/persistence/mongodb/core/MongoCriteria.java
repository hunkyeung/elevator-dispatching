package com.robustel.adapter.persistence.mongodb.core;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

/**
 * @author YangXuehong
 * @date 2022/3/4
 */
public interface MongoCriteria {
    Criteria toCriteria(Criteria criteria);

    static MongoCriteria ofEq(String field, Object value) {
        return new EqCriteria(field, value);
    }

    static MongoCriteria ofGt(String field, Object value) {
        return new GtCriteria(field, value);
    }

    static MongoCriteria ofGte(String field, Object value) {
        return new GteCriteria(field, value);
    }

    static MongoCriteria ofLt(String field, Object value) {
        return new LtCriteria(field, value);
    }

    static MongoCriteria ofLte(String field, Object value) {
        return new LteCriteria(field, value);
    }

    static MongoCriteria ofIn(String field, Object value) {
        return new InCriteria(field, value);
    }

    static MongoCriteria ofNin(String field, Object value) {
        return new NinCriteria(field, value);
    }

    static MongoCriteria ofLike(String field, Object value) {
        return new LikeCriteria(field, value);
    }

    static MongoCriteria ofBetween(String field, Object value) {
        return new BetweenCriteria(field, value);
    }

    @Getter(AccessLevel.PROTECTED)
    abstract class BaseCriteria implements MongoCriteria {
        private String field;
        private Object value;

        public BaseCriteria(String field, Object value) {
            this.field = field;
            this.value = value;
        }
    }

    class EqCriteria extends BaseCriteria {
        public EqCriteria(String field, Object value) {
            super(field, value);
        }

        @Override
        public Criteria toCriteria(Criteria criteria) {
            return criteria.and(getField()).is(getValue());
        }
    }

    class GtCriteria extends BaseCriteria {
        public GtCriteria(String field, Object value) {
            super(field, value);
        }

        @Override
        public Criteria toCriteria(Criteria criteria) {
            return criteria.and(getField()).gt(getValue());
        }
    }

    class GteCriteria extends BaseCriteria {
        public GteCriteria(String field, Object value) {
            super(field, value);
        }

        @Override
        public Criteria toCriteria(Criteria criteria) {
            return criteria.and(getField()).gte(getValue());
        }
    }

    class LtCriteria extends BaseCriteria {
        public LtCriteria(String field, Object value) {
            super(field, value);
        }

        @Override
        public Criteria toCriteria(Criteria criteria) {
            return criteria.and(getField()).lt(getValue());
        }
    }

    class LteCriteria extends BaseCriteria {
        public LteCriteria(String field, Object value) {
            super(field, value);
        }

        @Override
        public Criteria toCriteria(Criteria criteria) {
            return criteria.and(getField()).lte(getValue());
        }
    }

    class InCriteria extends BaseCriteria {
        public InCriteria(String field, Object value) {
            super(field, value);
        }

        @Override
        public Criteria toCriteria(Criteria criteria) {
            return criteria.and(getField()).in((List) getValue());
        }
    }

    class NinCriteria extends BaseCriteria {
        public NinCriteria(String field, Object value) {
            super(field, value);
        }

        @Override
        public Criteria toCriteria(Criteria criteria) {
            return criteria.and(getField()).nin((List) getValue());
        }
    }

    class LikeCriteria extends BaseCriteria {
        public LikeCriteria(String field, Object value) {
            super(field, value);
        }

        @Override
        public Criteria toCriteria(Criteria criteria) {
            return criteria.and(getField()).regex(".*?" + getValue() + ".*");
        }
    }

    class BetweenCriteria extends BaseCriteria {
        public BetweenCriteria(String field, Object value) {
            super(field, value);
        }

        @Override
        public Criteria toCriteria(Criteria criteria) {
            List<Object> list = (List) getValue();
            if (list.size() != 2) {
                throw new IllegalArgumentException("The size of list must be equal +" + list.size());
            }
            return criteria.and(getField()).gte(list.get(0)).lte(list.get(1));
        }
    }
}
