package persistence.entitymanager.event.event;

import persistence.entitymanager.Session;

public class PersistEvent implements Event {
    private final Object entity;
    private final Session session;
    private final boolean hasNewEntity;

    public PersistEvent(Object entity, Session session) {
        this.entity = entity;
        this.session = session;
        this.hasNewEntity = session.getPersistenceContext().guessEntityIsNewOrNot(entity);
    }

    public Object getEntity() {
        return entity;
    }

    public Session getSession() {
        return session;
    }

    public boolean hasNewEntity() {
        return hasNewEntity;
    }
}
