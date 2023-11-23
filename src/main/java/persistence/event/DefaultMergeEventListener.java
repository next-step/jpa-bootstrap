package persistence.event;

import persistence.action.ActionQueue;
import persistence.action.EntityUpdateAction;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisters;

public class DefaultMergeEventListener implements MergeEventListener {

    private final ActionQueue actionQueue;
    private final EntityPersisters entityPersisters;

    public DefaultMergeEventListener(final ActionQueue actionQueue, final EntityPersisters entityPersisters) {
        this.actionQueue = actionQueue;
        this.entityPersisters = entityPersisters;
    }

    @Override
    public void onMerge(final MergeEvent mergeEvent) {
        final EntityPersister entityPersister = entityPersisters.getEntityPersister(mergeEvent.getTargetClass());
        actionQueue.addUpdate(new EntityUpdateAction(entityPersister, mergeEvent.getTarget()));
    }
}
