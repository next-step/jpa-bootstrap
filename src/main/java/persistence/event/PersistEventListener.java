package persistence.event;

import persistence.action.ActionQueue;
import persistence.action.EntityInsertAction;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisters;

public class PersistEventListener implements EventListener {
    private final ActionQueue actionQueue;
    private final EntityPersisters entityPersisters;

    public PersistEventListener(final ActionQueue actionQueue, final EntityPersisters entityPersisters) {
        this.actionQueue = actionQueue;
        this.entityPersisters = entityPersisters;
    }

    @Override
    public <T> T on(final Event<T> event) {
        final EntityPersister entityPersister = entityPersisters.getEntityPersister(event.getTargetClass());
        actionQueue.addInsertion(new EntityInsertAction(entityPersister, event.getTarget()));
        return event.getTarget();
    }
}
