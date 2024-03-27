package persistence.entitymanager.event.listeners;

import persistence.bootstrap.Metadata;
import persistence.entity.context.PersistenceContext;
import persistence.entity.context.PersistentClass;
import persistence.entitymanager.event.event.LoadEvent;

public class LoadEventListener {
    private final Metadata metadata;

    public LoadEventListener(Metadata metadata) {
        this.metadata = metadata;
    }

    public void onLoad(LoadEvent event) {
        PersistenceContext persistenceContext = event.getPersistenceContext();
        PersistentClass<?> persistentClass = metadata.getPersistentClass(event.getEntityClass());
        Object result = persistenceContext.getEntity(persistentClass, event.getId());

        event.setResult(result);
    }
}
