package persistence.event;

import persistence.entity.EntityEntry;

import java.io.Serializable;

public class LoadEvent extends AbstractEvent {

    private final Serializable identifier;
    private final Object entity;
    private final EntityEntry entityEntry;

    private LoadEvent(EventSource source,
                      Serializable identifier,
                      Object entity,
                      EntityEntry entityEntry) {

        super(source);
        this.identifier = identifier;
        this.entity = entity;
        this.entityEntry = entityEntry;
    }

    public static LoadEvent create(EventSource source,
                                   Serializable identifier,
                                   Object entity,
                                   EntityEntry entityEntry) {

        return new LoadEvent(source, identifier, entity, entityEntry);
    }

    public Serializable getIdentifier() {
        return identifier;
    }

    public Object getEntity() {
        return entity;
    }

    public EntityEntry getEntityEntry() {
        return entityEntry;
    }
}
