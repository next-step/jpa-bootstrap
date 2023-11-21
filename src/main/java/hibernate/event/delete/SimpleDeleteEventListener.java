package hibernate.event.delete;

import hibernate.action.DeleteActionQueue;
import hibernate.action.EntityDeleteAction;
import hibernate.metamodel.MetaModel;

public class SimpleDeleteEventListener implements DeleteEventListener {

    private final MetaModel metaModel;
    private final DeleteActionQueue actionQueue;

    public SimpleDeleteEventListener(final MetaModel metaModel, final DeleteActionQueue actionQueue) {
        this.metaModel = metaModel;
        this.actionQueue = actionQueue;
    }

    @Override
    public <T> void onDelete(final DeleteEvent<T> event) {
        actionQueue.addAction(new EntityDeleteAction<>(metaModel.getEntityPersister(event.getClazz()), event.getEntity()));
    }
}
