package event.impl;

import event.Event;
import persistence.sql.clause.Clause;
import persistence.sql.dml.EntityManager;
import persistence.sql.dml.MetadataLoader;

public record SaveOrUpdateEvent<T>(
        T entity,
        Object primaryKey,
        MetadataLoader<T> metadataLoader,
        EntityManager entityManager) implements Event {

    @SuppressWarnings("unchecked")
    public static <T> SaveOrUpdateEvent<T> create(T entity, EntityManager entityManager) {
        MetadataLoader<T> loader = entityManager.getMetadataLoader((Class<T>) entity.getClass());
        Object primaryKey = Clause.extractValue(loader.getPrimaryKeyField(), entity);

        return new SaveOrUpdateEvent<>(entity, primaryKey, loader, entityManager);
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

    public Class<T> entityType() {
        return metadataLoader.getEntityType();
    }
}
