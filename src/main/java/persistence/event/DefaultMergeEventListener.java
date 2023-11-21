package persistence.event;

import persistence.action.ActionQueueRear;
import persistence.action.EntityUpdateAction;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisters;

public class DefaultMergeEventListener implements MergeEventListener {

    private final ActionQueueRear actionQueueRear;
    private final EntityPersisters entityPersisters;

    public DefaultMergeEventListener(final ActionQueueRear actionQueueRear, final EntityPersisters entityPersisters) {
        this.actionQueueRear = actionQueueRear;
        this.entityPersisters = entityPersisters;
    }

    @Override
    public void onMerge(final MergeEvent mergeEvent) {
        final EntityPersister entityPersister = entityPersisters.getEntityPersister(mergeEvent.getTargetClass());
        actionQueueRear.addUpdate(new EntityUpdateAction(entityPersister, mergeEvent.getTarget()));
    }
}
