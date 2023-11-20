package persistence.event;

import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisters;

public class DefaultDeleteEventListener implements DeleteEventListener {

    private final EntityPersisters entityPersisters;

    public DefaultDeleteEventListener(final EntityPersisters entityPersisters) {
        this.entityPersisters = entityPersisters;
    }

    @Override
    public void onDelete(final DeleteEvent deleteEvent) {
        final EntityPersister entityPersister = entityPersisters.getEntityPersister(deleteEvent.getTargetClass());
        entityPersister.delete(deleteEvent.getTarget());
    }
}
