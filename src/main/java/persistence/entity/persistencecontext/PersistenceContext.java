package persistence.entity.persistencecontext;

import persistence.entity.EntityEntry;

public interface PersistenceContext {

    <T> Object getEntity(Class<T> clazz, Object id);

    <T> void addEntity(T entity);

    <T> void removeEntity(T entity);

    <T> EntitySnapshot getCachedDatabaseSnapshot(T entity);

    <T> EntitySnapshot getDatabaseSnapshot(T entity);

    <T> void setEntityEntry(T entity, EntityEntry entityEntry);

    <T> EntityEntry getEntityEntry(T entity);
}
