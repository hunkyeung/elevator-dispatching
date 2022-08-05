package com.robustel.adapter.iot.thing.persistence.memory;

import com.robustel.adapter.persistence.mongodb.PersistentObject;
import com.robustel.thing.domain.thing_status.ThingStatus;
import com.robustel.utils.ThreadLocalUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ThingStatusRepositoryInMemoryAtRuntimeTest {

    private ThingStatusRepositoryInMemoryAtRuntime thingStatusRepositoryInMemoryAtRuntime;

    @BeforeEach
    void init() {
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);
        ThingStatus status1 = mock(ThingStatus.class);
        when(status1.getId()).thenReturn("1");
        ThingStatus status2 = mock(ThingStatus.class);
        when(status2.getId()).thenReturn("2");
        ThingStatus status3 = mock(ThingStatus.class);
        when(status3.getId()).thenReturn("3");
        ThingStatus status4 = mock(ThingStatus.class);
        when(status4.getId()).thenReturn("4");
        when(mongoTemplate.findAll(PersistentObject.class, "th_thing_status")).thenReturn(
                List.of(new PersistentObject<>("1", "robustel", status1),
                        new PersistentObject<>("2", "robustel2", status2),
                        new PersistentObject<>("3", "robustel", status3),
                        new PersistentObject<>("4", "robustel4", status4))
        );
        thingStatusRepositoryInMemoryAtRuntime = new ThingStatusRepositoryInMemoryAtRuntime(mongoTemplate);
        thingStatusRepositoryInMemoryAtRuntime.init();
    }

    @Test
    void testInit() {
        assertTrue(thingStatusRepositoryInMemoryAtRuntime.getThingStatusTenantMap().containsKey("robustel"));
        assertTrue(thingStatusRepositoryInMemoryAtRuntime.getThingStatusTenantMap().containsKey("robustel2"));
        assertTrue(thingStatusRepositoryInMemoryAtRuntime.getThingStatusTenantMap().containsKey("robustel4"));
        assertFalse(thingStatusRepositoryInMemoryAtRuntime.getThingStatusTenantMap().containsKey("robustel5"));
        assertEquals(Set.of("1", "3"), thingStatusRepositoryInMemoryAtRuntime.getThingStatusTenantMap().get("robustel").keySet());
        assertEquals(Set.of("2"), thingStatusRepositoryInMemoryAtRuntime.getThingStatusTenantMap().get("robustel2").keySet());
        assertFalse(thingStatusRepositoryInMemoryAtRuntime.getThingStatusTenantMap().get("robustel").containsKey("4"));
    }

    @Test
    void testSaveForNewTenant() {
        ThreadLocalUtil.set("TENANT_ID", "robustel5");
        ThingStatus status5 = mock(ThingStatus.class);
        when(status5.getId()).thenReturn("5");
        thingStatusRepositoryInMemoryAtRuntime.save(status5);
        assertTrue(thingStatusRepositoryInMemoryAtRuntime.getThingStatusTenantMap().containsKey("robustel5"));
        assertEquals(status5, thingStatusRepositoryInMemoryAtRuntime.getThingStatusTenantMap().get("robustel5").get("5"));
        ThreadLocalUtil.remove("TENANT_ID");
    }

    @Test
    void testSaveForNewStatus() {
        ThreadLocalUtil.set("TENANT_ID", "robustel2");
        ThingStatus status5 = mock(ThingStatus.class);
        when(status5.getId()).thenReturn("5");
        thingStatusRepositoryInMemoryAtRuntime.save(status5);
        assertEquals(status5, thingStatusRepositoryInMemoryAtRuntime.getThingStatusTenantMap().get("robustel2").get("5"));
        ThreadLocalUtil.remove("TENANT_ID");
    }

    @Test
    void testSave() {
        ThreadLocalUtil.set("TENANT_ID", "robustel");
        ThingStatus newStatus1 = mock(ThingStatus.class);
        when(newStatus1.getId()).thenReturn("1");
        assertNotEquals(newStatus1, thingStatusRepositoryInMemoryAtRuntime.getThingStatusTenantMap().get("robustel").get("1"));
        thingStatusRepositoryInMemoryAtRuntime.save(newStatus1);
        assertEquals(newStatus1, thingStatusRepositoryInMemoryAtRuntime.getThingStatusTenantMap().get("robustel").get("1"));
        ThreadLocalUtil.remove("TENANT_ID");
    }

    @Test
    void testDelete() {
        ThreadLocalUtil.set("TENANT_ID", "robustel");
        ThingStatus status1 = thingStatusRepositoryInMemoryAtRuntime.getThingStatusTenantMap().get("robustel").get("1");
        thingStatusRepositoryInMemoryAtRuntime.delete(status1);
        assertNull(thingStatusRepositoryInMemoryAtRuntime.getThingStatusTenantMap().get("robustel").get("1"));
        ThreadLocalUtil.remove("TENANT_ID");
    }

    @Test
    void testDeleteById() {
        ThreadLocalUtil.set("TENANT_ID", "robustel");
        thingStatusRepositoryInMemoryAtRuntime.deleteById("1");
        assertNull(thingStatusRepositoryInMemoryAtRuntime.getThingStatusTenantMap().get("robustel").get("1"));
        ThreadLocalUtil.remove("TENANT_ID");
    }

    @Test
    void testFindByExistId() {
        ThreadLocalUtil.set("TENANT_ID", "robustel");
        Optional<ThingStatus> status1 = thingStatusRepositoryInMemoryAtRuntime.findById("1");
        assertFalse(status1.isEmpty());
        ThreadLocalUtil.remove("TENANT_ID");
    }

    @Test
    void testFindByNotExistId() {
        ThreadLocalUtil.set("TENANT_ID", "robustel");
        Optional<ThingStatus> status1 = thingStatusRepositoryInMemoryAtRuntime.findById("10");
        assertTrue(status1.isEmpty());
        ThreadLocalUtil.remove("TENANT_ID");
    }

    @Test
    void testFindAll() {
        ThreadLocalUtil.set("TENANT_ID", "robustel");
        List<ThingStatus> all = thingStatusRepositoryInMemoryAtRuntime.findAll();
        assertEquals(thingStatusRepositoryInMemoryAtRuntime.getThingStatusTenantMap().get("robustel").values().stream().toList(), all);
        ThreadLocalUtil.remove("TENANT_ID");
    }
}