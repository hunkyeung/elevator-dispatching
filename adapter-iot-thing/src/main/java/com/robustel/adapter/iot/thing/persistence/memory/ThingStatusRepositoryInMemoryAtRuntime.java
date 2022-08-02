package com.robustel.adapter.iot.thing.persistence.memory;

import com.robustel.adapter.persistence.mongodb.PersistentObject;
import com.robustel.adapter.persistence.mongodb.thing.ThingStatusRepositoryMongoDB;
import com.robustel.thing.domain.thing_status.ThingStatus;
import com.robustel.thing.domain.thing_status.ThingStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;

@Repository
@Slf4j
public class ThingStatusRepositoryInMemoryAtRuntime extends ThingStatusRepositoryMongoDB implements ThingStatusRepository {
    private Map<String, Map<String, ThingStatus>> thingStatusTenantMap;

    public ThingStatusRepositoryInMemoryAtRuntime(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    @PostConstruct
    public void init() {
        thingStatusTenantMap = new HashMap<>();
        mongoTemplate.findAll(PersistentObject.class, this.collectionName()).forEach(
                persistentObject -> {
                    String tenantId = persistentObject.getTenantId();
                    if (!thingStatusTenantMap.containsKey(tenantId)) {
                        thingStatusTenantMap.put(tenantId, new HashMap<>());
                    }
                    ThingStatus entity = (ThingStatus) persistentObject.getEntity();
                    thingStatusTenantMap.get(tenantId).put(entity.getId(), entity);
                }
        );

    }

    @Override
    public ThingStatus save(ThingStatus thingStatus) {
        if (!thingStatusTenantMap.containsKey(getTenant())) {
            thingStatusTenantMap.put(getTenant(), new HashMap<>());
        }
        Map<String, ThingStatus> tenantMap = thingStatusTenantMap.get(getTenant());
        if (Objects.isNull(tenantMap.get(thingStatus.getId()))) {
            super.save(thingStatus);
        }
        tenantMap.put(thingStatus.getId(), thingStatus);
        return thingStatus;
    }

    @Override
    public void delete(ThingStatus thingStatus) {
        super.delete(thingStatus);
        Optional.ofNullable(thingStatusTenantMap.get(getTenant())).orElse(new HashMap<>()).remove(thingStatus.getId(), thingStatus);
    }

    @Override
    public void deleteById(String id) {
        super.deleteById(id);
        Optional.ofNullable(thingStatusTenantMap.get(getTenant())).orElse(new HashMap<>()).remove(id);
    }

    @Override
    public Optional<ThingStatus> findById(String id) {
        return Optional.ofNullable(Optional.ofNullable(thingStatusTenantMap.get(getTenant())).orElse(new HashMap<>()).get(id));
    }

    @Override
    public List<ThingStatus> findAll() {
        return Optional.ofNullable(thingStatusTenantMap.get(getTenant())).orElse(Map.of()).values().stream().toList();
    }

}
