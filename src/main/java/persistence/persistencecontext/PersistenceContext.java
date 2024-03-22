package persistence.persistencecontext;

import persistence.entity.EntityEntryStatus;

import java.util.List;
import java.util.Optional;

public interface PersistenceContext {
    Optional<Object> getEntity(Class<?> clazz, Object id);

    void addEntity(Object id, Object entity);

    void removeEntity(Object id, Object entity);

    Object getDatabaseSnapshot(Object id, Object entity);

    Object getCachedDatabaseSnapshot(Object id, Object entity);

    void addEntityEntry(Object entity, EntityEntryStatus entityEntryStatus);

    List<Object> getDirtyEntities();
}
