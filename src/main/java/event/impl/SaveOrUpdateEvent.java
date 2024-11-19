package event.impl;

import event.Event;
import persistence.sql.clause.Clause;
import persistence.sql.dml.EntityManager;
import persistence.sql.dml.MetadataLoader;

public record SaveOrUpdateEvent(
        Object entity,
        Object primaryKey,
        MetadataLoader<?> metadataLoader,
        EntityManager entityManager) implements Event {

    public static SaveOrUpdateEvent create(Object entity, EntityManager entityManager) {
        MetadataLoader<?> loader = entityManager.getMetadataLoader(entity.getClass());
        Object primaryKey = Clause.extractValue(loader.getPrimaryKeyField(), entity);

        return new SaveOrUpdateEvent(entity, primaryKey, loader, entityManager);
    }

    @Override
    public String entityName() {
        return metadataLoader.getEntityName();
    }

    @Override
    public Object entityId() {
        return primaryKey;
    }

    @Override
    public EntityManager entityManager() {
        return entityManager;
    }

    public Class<?> entityType() {
        return entity.getClass();
    }
}
