package com.robustel.adapter.iot.thing.persistence.memory;

import com.robustel.adapter.persistence.mongodb.PersistentObject;
import com.robustel.adapter.persistence.mongodb.thing.ThingStatusRepositoryMongoDB;
import com.robustel.thing.domain.thing_status.ThingStatus;
import com.robustel.thing.domain.thing_status.ThingStatusRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
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
            super.save(thingStatus);
            Map<String, ThingStatus> thingStatusMap = new HashMap<>();
            thingStatusMap.put(thingStatus.getId(), thingStatus);
            thingStatusTenantMap.put(getTenant(), thingStatusMap);
        }
        return thingStatus;
    }

    @Override
    public void delete(ThingStatus thingStatus) {
        super.delete(thingStatus);
        Optional.ofNullable(thingStatusTenantMap.get(getTenant())).orElse(new HashMap<>()).remove(thingStatus.getId());
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
        return Optional.ofNullable(thingStatusTenantMap.get(getTenant())).orElse(Map.of()).values().stream().collect(Collectors.toList());
    }

    public List<ThingStatus> findWithLocationGroup() {
        return null;
    }
}
