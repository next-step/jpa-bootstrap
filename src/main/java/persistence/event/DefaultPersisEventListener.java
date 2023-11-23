package persistence.event;

import persistence.action.ActionQueue;
import persistence.action.EntityInsertAction;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisters;

public class DefaultPersisEventListener implements PersistEventListener {

    private final ActionQueue actionQueue;
    private final EntityPersisters entityPersisters;

    public DefaultPersisEventListener(final ActionQueue actionQueue, final EntityPersisters entityPersisters) {
        this.actionQueue = actionQueue;
        this.entityPersisters = entityPersisters;
    }

    @Override
    public void onPersist(final PersistEvent persistEvent) {
        final EntityPersister entityPersister = entityPersisters.getEntityPersister(persistEvent.getTargetClass());
        actionQueue.addInsertion(new EntityInsertAction(entityPersister, persistEvent.getTarget()));
    }
}
