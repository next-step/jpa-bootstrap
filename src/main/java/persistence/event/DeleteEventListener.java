package persistence.event;

import persistence.action.ActionQueue;
import persistence.action.EntityDeleteAction;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisters;

public class DeleteEventListener implements EventListener {

    private final ActionQueue actionQueue;
    private final EntityPersisters entityPersisters;

    public DeleteEventListener(final ActionQueue actionQueue, final EntityPersisters entityPersisters) {
        this.actionQueue = actionQueue;
        this.entityPersisters = entityPersisters;
    }

    @Override
    public <T> T on(final Event<T> event) {
        final EntityPersister entityPersister = entityPersisters.getEntityPersister(event.getTargetClass());
        actionQueue.addDeletion(new EntityDeleteAction(entityPersister, event.getTarget()));
        return event.getTarget();
    }
}
