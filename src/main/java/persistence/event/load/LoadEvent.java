package persistence.event.load;

import persistence.entity.EntityEntry;
import persistence.event.AbstractEvent;
import persistence.event.EventSource;

import java.io.Serializable;

public class LoadEvent<T> extends AbstractEvent {

    private final Serializable identifier;
    private final Class<T> entityClass;
    private final EntityEntry entityEntry;

    private T resultEntity;

    public LoadEvent(EventSource source,
                     Class<T> entityClass,
                     Serializable identifier,
                     EntityEntry entityEntry) {

        super(source);
        this.identifier = identifier;
        this.entityClass = entityClass;
        this.resultEntity = null;
        this.entityEntry = entityEntry;
    }

    public Serializable getIdentifier() {
        return identifier;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public EntityEntry getEntityEntry() {
        return entityEntry;
    }

    public T getResultEntity() {
        return resultEntity;
    }

    public void setResultEntity(T resultEntity) {
        this.resultEntity = resultEntity;
    }
}
