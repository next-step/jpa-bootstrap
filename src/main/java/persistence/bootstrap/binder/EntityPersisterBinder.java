package persistence.bootstrap.binder;

import jdbc.JdbcTemplate;
import persistence.entity.persister.EntityPersister;
import persistence.meta.EntityTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityPersisterBinder {
    private final Map<String, EntityPersister> entityPersisterRegistry = new HashMap<>();

    public EntityPersisterBinder(List<Class<?>> entityTypes, EntityTableBinder entityTableBinder,
                                 JdbcTemplate jdbcTemplate) {
        for (Class<?> entityType : entityTypes) {
            final EntityTable entityTable = entityTableBinder.getEntityTable(entityType);
            final EntityPersister entityPersister = new EntityPersister(entityTable, jdbcTemplate);
            entityPersisterRegistry.put(entityType.getTypeName(), entityPersister);
        }
    }

    public EntityPersister getEntityPersister(Class<?> entityType) {
        return entityPersisterRegistry.get(entityType.getName());
    }

    public void clear() {
        entityPersisterRegistry.clear();
    }
}
