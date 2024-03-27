package persistence.entitymanager.event.event;

import persistence.entity.context.PersistenceContext;

public class PersistEvent implements Event {
    private final Object entity;
    private final PersistenceContext persistenceContext;

    public PersistEvent(Object entity, PersistenceContext persistenceContext) {
        this.entity = entity;
        this.persistenceContext = persistenceContext;
    }

    public Object getEntity() {
        return entity;
    }

    public PersistenceContext getPersistenceContext() {
        return persistenceContext;
    }
}
