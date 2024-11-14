package persistence.event.delete;

import persistence.action.EntityDeleteAction;
import persistence.entity.EntityPersister;
import persistence.entity.Status;

public class DefaultDeleteEventListener implements DeleteEventListener {

    @Override
    public void onDelete(DeleteEvent event) {
        final EntityPersister persister = event.getSession().findEntityPersister(event.getEntity().getClass());

        event.getEntry().updateStatus(Status.DELETED);
        event.getSession().getActionQueue().addAction(
                new EntityDeleteAction(event.getSession(), event.getEntity(), persister)
        );
    }
}
