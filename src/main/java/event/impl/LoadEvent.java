package event.impl;

import event.Event;
import persistence.sql.dml.EntityManager;
import persistence.sql.dml.MetadataLoader;

public record LoadEvent<T>(MetadataLoader<T> metadataLoader, Object entityId,
                           EntityManager entityManager) implements Event<T> {

    public static <T> LoadEvent<T> create(Class<T> returnType, Object primaryKey, EntityManager entityManager) {
        MetadataLoader<T> metadataLoader = entityManager.getMetadataLoader(returnType);

        return new LoadEvent<>(metadataLoader, primaryKey, entityManager);
    }

    @Override
    public T entity() {
        return null;
    }

    @Override
    public String entityName() {
        return metadataLoader.getEntityName();
    }
}
