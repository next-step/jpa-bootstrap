package persistence.entitymanager.event.listeners;

import persistence.entity.context.PersistenceContext;
import persistence.entitymanager.event.event.DeleteEvent;

public class DeleteEventListener implements EventListener {
    public void onDelete(DeleteEvent event) {
        PersistenceContext persistenceContext = event.getPersistenceContext();
        persistenceContext.removeEntity(event.getEntity());
    }
}
