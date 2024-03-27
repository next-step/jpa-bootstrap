package persistence.entitymanager.event.event;

import persistence.entitymanager.Session;

public class DeleteEvent implements Event {
    private final Object entity;
    private final Session session;

    public DeleteEvent(Object entity, Session session) {
        this.entity = entity;
        this.session = session;
    }

    public Object getEntity() {
        return entity;
    }

    public Session getSession() {
        return session;
    }
}
