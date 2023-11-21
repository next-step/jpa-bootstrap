package persistence.event;

import persistence.action.ActionQueueRear;
import persistence.action.EntityInsertAction;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisters;

public class DefaultPersisEventListener implements PersistEventListener {

    private final ActionQueueRear actionQueueRear;
    private final EntityPersisters entityPersisters;

    public DefaultPersisEventListener(final ActionQueueRear actionQueueRear, final EntityPersisters entityPersisters) {
        this.actionQueueRear = actionQueueRear;
        this.entityPersisters = entityPersisters;
    }

    @Override
    public void onPersist(final PersistEvent persistEvent) {
        final EntityPersister entityPersister = entityPersisters.getEntityPersister(persistEvent.getTargetClass());
        actionQueueRear.addInsertion(new EntityInsertAction(entityPersister, persistEvent.getTarget()));
    }
}
