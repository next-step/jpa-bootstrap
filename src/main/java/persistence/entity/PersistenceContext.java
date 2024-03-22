package persistence.entity;

import persistence.entity.event.delete.DeleteEvent;
import persistence.entity.event.update.UpdateEvent;

import java.util.Optional;
import java.util.Queue;

public interface PersistenceContext {

    <T> Optional<T> getEntity(Class<T> clazz, Object id);

    void addEntity(Object entity, Object id);

    void removeEntity(Class<?> clazz, Object id);

    EntityMetaData getDatabaseSnapshot(EntityMetaData entityMetaData, Object id);

    <T> EntityMetaData getSnapshot(T entity, Object id);

    void clear();
}
