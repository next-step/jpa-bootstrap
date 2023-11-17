package hibernate.event.delete;

import hibernate.action.EntityDeleteAction;

public class SimpleDeleteEventListener implements DeleteEventListener {

    @Override
    public <T> void onDelete(final DeleteEvent<T> event) {
        event.getActionQueue()
                .addAction(new EntityDeleteAction<>(event.getEntityPersister(), event.getEntity()));
    }
}
