package persistence.entitymanager.event.listeners;

import persistence.entity.context.PersistenceContext;
import persistence.entitymanager.event.event.PersistEvent;

public class PersistEventListener {
    public void onPersist(PersistEvent event) {
        PersistenceContext persistenceContext = event.getPersistenceContext();
        persistenceContext.addEntity(event.getEntity());
    }
}
