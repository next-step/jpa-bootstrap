package persistence.event.persist;

import persistence.action.EntityInsertAction;
import persistence.entity.EntityEntry;
import persistence.entity.EntityPersister;
import persistence.event.EventSource;

public class DefaultPersistEventListener implements PersistEventListener {

    @Override
    public void onPersist(PersistEvent event) {
        final EventSource source = event.getSession();
        final Object entity = event.getEntity();
        final EntityEntry entry = EntityEntry.inSaving();

        final EntityPersister persister = source.findEntityPersister(entity.getClass());

        event.getSession().getActionQueue().addAction(
                new EntityInsertAction(source, entity, persister, entry)
        );
    }

}
