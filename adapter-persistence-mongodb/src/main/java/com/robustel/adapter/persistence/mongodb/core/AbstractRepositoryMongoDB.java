package com.robustel.adapter.persistence.mongodb.core;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.yeung.api.util.query.*;
import org.yeung.core.Entity;
import org.yeung.core.Repository;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author YangXuehong
 * @date 2021/8/20
 */
public abstract class AbstractRepositoryMongoDB<E extends Entity, I extends Serializable>
        implements Repository<E, I> {
    protected MongoTemplate mongoTemplate;
    protected MongoPageHelper mongoPageHelper;

    protected AbstractRepositoryMongoDB(MongoTemplate mongoTemplate, MongoPageHelper mongoPageHelper) {
        this.mongoTemplate = mongoTemplate;
        this.mongoPageHelper = mongoPageHelper;
    }

    protected String collectionName() {
        return this.getClass().getSimpleName();
    }

    public E save(E e) {
        mongoTemplate.save(e, collectionName());
        return e;
    }

    public void delete(E e) {
        mongoTemplate.remove(e, collectionName());
    }

    public void deleteById(I id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, collectionName());
    }

    public List<E> findAll() {
        return mongoTemplate.findAll(Entity.class, collectionName()).stream().map(
                entity -> (E) entity
        ).collect(Collectors.toList());
    }

    public Optional<E> findById(I id) {
        return Optional.ofNullable((E) mongoTemplate.findById(id, Entity.class, collectionName()));
    }

    public long countByCriteria(List<Matching> matchings, KeyWord keyWord) {
        Query query = Query.query(buildCriteria(matchings, keyWord));
        return mongoTemplate.count(query, collectionName());
    }

    public Optional<E> findOneByCriteria(List<Matching> matchings, List<Sort> sorts) {
        Query query = Query.query(buildCriteria(matchings, null));
        query.with(org.springframework.data.domain.Sort.by(buildOrder(sorts)));
        Entity entity = mongoTemplate.findOne(query, Entity.class, collectionName());
        return Optional.ofNullable((E) entity);
    }

    public List<E> findByCriteria(org.yeung.api.util.query.Query query) {
        return findByCriteria(query.getMatchings(), query.getKeyWord(), query.getSorts());
    }

    public PageResult<E> findByCriteria(org.yeung.api.util.query.Query query, Page page) {
        return findByCriteria(query.getMatchings(), query.getKeyWord(), query.getSorts(), page);
    }

    public List<E> findByCriteria(List<Matching> matchings, KeyWord keyWord, List<Sort> sorts) {
        Query query = Query.query(buildCriteria(matchings, keyWord));
        query.with(org.springframework.data.domain.Sort.by(buildOrder(sorts)));
        List<Entity> entities = mongoTemplate.find(query, Entity.class, collectionName());
        return entities.stream().map(entity -> (E) entity).collect(Collectors.toList());
    }

    public PageResult<E> findByCriteria(List<Matching> matchings, KeyWord keyWord, List<Sort> sorts, Page page) {
        Query query = Query.query(buildCriteria(matchings, keyWord));
        query.with(org.springframework.data.domain.Sort.by(buildOrder(sorts)));
        PageResult<Entity> result = mongoPageHelper.pageQuery(query, Entity.class, page.getSize(), page.getNumber(), collectionName());
        return new PageResult<>(result.getPageNum(),
                result.getPageSize(), result.getTotal(), result.getPages(),
                result.getList().stream().map(entity -> (E) entity).collect(Collectors.toList()));
    }

    protected Criteria buildCriteria(List<Matching> matchings, KeyWord keyWord) {
        Criteria criteria = new Criteria();
        Optional.ofNullable(matchings).orElse(new ArrayList<>(0)).stream().map(
                matching ->
                        CriteriaType.of(matching, fieldMap())
        ).collect(Collectors.toList()).stream().forEach(
                mongoCriteria -> mongoCriteria.toCriteria(criteria)
        );
        if (keyWord != null) {
            String regexValue = ".*?" + keyWord.getValue() + ".*";
            List<Criteria> criteriaList = keyWord.getFields().stream().map(
                    field -> Criteria.where(field.getValue(fieldMap())).regex(regexValue)).collect(Collectors.toList());
            Criteria[] criteriaArray = new Criteria[criteriaList.size()];
            criteria.orOperator(criteriaList.toArray(criteriaArray));
        }
        return criteria;
    }

    private List<org.springframework.data.domain.Sort.Order> buildOrder(List<Sort> sorts) {
        return Optional.ofNullable(sorts).orElse(new ArrayList<>(0))
                .stream().sorted(Comparator.comparing(Sort::getOrder)).map(
                        sort -> new org.springframework.data.domain.Sort.Order(
                                org.springframework.data.domain.Sort.Direction.valueOf(sort.getDirection().name()), sort.getField().getValue(fieldMap()))
                ).collect(Collectors.toList());
    }

    //如传递的字段与数据库字段不一致，则在这里映射转换
    protected Map<String, String> fieldMap() {
        return new HashMap<>(0);
    }


}
