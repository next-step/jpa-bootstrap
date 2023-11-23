package persistence.event;

import persistence.action.ActionQueue;
import persistence.action.EntityDeleteAction;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisters;

public class DefaultDeleteEventListener implements DeleteEventListener {
    private final ActionQueue actionQueue;
    private final EntityPersisters entityPersisters;

    public DefaultDeleteEventListener(final ActionQueue actionQueue, final EntityPersisters entityPersisters) {
        this.actionQueue = actionQueue;
        this.entityPersisters = entityPersisters;
    }

    @Override
    public void onDelete(final DeleteEvent deleteEvent) {
        final EntityPersister entityPersister = entityPersisters.getEntityPersister(deleteEvent.getTargetClass());
        actionQueue.addDeletion(new EntityDeleteAction(entityPersister, deleteEvent.getTarget()));
    }
}
