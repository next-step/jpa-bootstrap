package event.impl;

import event.Event;
import persistence.sql.clause.Clause;
import persistence.sql.dml.EntityManager;
import persistence.sql.dml.MetadataLoader;

public record DeleteEvent<T>(T entity,
                          String entityName,
                          Object entityId,
                          MetadataLoader<T> metadataLoader,
                          EntityManager entityManager) implements Event<T> {

    @SuppressWarnings("unchecked")
    public static <T> DeleteEvent<T> create(T entity, EntityManager entityManager) {
        MetadataLoader<T> loader = entityManager.getMetadataLoader((Class<T>) entity.getClass());
        Object primaryKey = Clause.extractValue(loader.getPrimaryKeyField(), entity);

        return new DeleteEvent<>(entity, loader.getEntityName(), primaryKey, loader, entityManager);
    }
}
