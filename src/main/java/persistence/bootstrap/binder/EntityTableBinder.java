package persistence.bootstrap.binder;

import persistence.meta.EntityTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityTableBinder {
    private final Map<String, EntityTable> entityTableRegistry = new HashMap<>();

    public EntityTableBinder(List<Class<?>> entityTypes) {
        for (Class<?> entityType : entityTypes) {
            final EntityTable entityTable = new EntityTable(entityType);
            entityTableRegistry.put(entityType.getTypeName(), entityTable);
        }
    }

    public EntityTable getEntityTable(Class<?> entityType) {
        return entityTableRegistry.get(entityType.getName());
    }
}
