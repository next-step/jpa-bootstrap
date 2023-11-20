package persistence.event;

import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisters;

public class DefaultMergeEventListener implements MergeEventListener {

    private final EntityPersisters entityPersisters;

    public DefaultMergeEventListener(final EntityPersisters entityPersisters) {
        this.entityPersisters = entityPersisters;
    }

    @Override
    public void onMerge(final MergeEvent mergeEvent) {
        final EntityPersister entityPersister = entityPersisters.getEntityPersister(mergeEvent.getTargetClass());
        entityPersister.update(mergeEvent.getTarget());
    }
}
