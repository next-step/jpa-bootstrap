package persistence.event;

import persistence.action.ActionQueueRear;
import persistence.action.EntityDeleteAction;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisters;

public class DefaultDeleteEventListener implements DeleteEventListener {
    private final ActionQueueRear actionQueueRear;
    private final EntityPersisters entityPersisters;

    public DefaultDeleteEventListener(final ActionQueueRear actionQueueRear, final EntityPersisters entityPersisters) {
        this.actionQueueRear = actionQueueRear;
        this.entityPersisters = entityPersisters;
    }

    @Override
    public void onDelete(final DeleteEvent deleteEvent) {
        final EntityPersister entityPersister = entityPersisters.getEntityPersister(deleteEvent.getTargetClass());
        actionQueueRear.addDeletion(new EntityDeleteAction(entityPersister, deleteEvent.getTarget()));
    }
}
