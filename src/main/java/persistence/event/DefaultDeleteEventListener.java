package persistence.event;

import persistence.entity.EntityKey;
import persistence.entity.EntityPersister;
import persistence.entity.Status;

public class DefaultDeleteEventListener implements DeleteEventListener {

    @Override
    public void onDelete(DeleteEvent event) {
        final EntityPersister persister = event.getSession().findEntityPersister(event.getEntity().getClass());

        event.getEntry().updateStatus(Status.DELETED);
        persister.delete(event.getEntity());
        event.getSession().getPersistenceContext().removeEntity(
                new EntityKey(persister.getEntityId(event.getEntity()), event.getEntity().getClass())
        );
    }
}
