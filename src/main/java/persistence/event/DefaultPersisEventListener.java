package persistence.event;

import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisters;

public class DefaultPersisEventListener implements PersistEventListener {

    private final EntityPersisters entityPersisters;

    public DefaultPersisEventListener(final EntityPersisters entityPersisters) {
        this.entityPersisters = entityPersisters;
    }

    @Override
    public void onPersist(final PersistEvent persistEvent) {
        final EntityPersister entityPersister = entityPersisters.getEntityPersister(persistEvent.getTargetClass());
        entityPersister.insert(persistEvent.getTarget());
    }
}
