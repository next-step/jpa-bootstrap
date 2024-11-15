package persistence.event;

import persistence.bootstrap.Metamodel;
import persistence.entity.persister.EntityPersister;

public class DefaultDeleteEventListener implements DeleteEventListener {
    @Override
    public <T> void onDelete(DeleteEvent<T> deleteEvent) {
        final Metamodel metamodel = deleteEvent.getMetamodel();
        final T entity = deleteEvent.getEntity();

        final EntityPersister entityPersister = metamodel.getEntityPersister(entity.getClass());
        entityPersister.delete(entity);
    }
}
