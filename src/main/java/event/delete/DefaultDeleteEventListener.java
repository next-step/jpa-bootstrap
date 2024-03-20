package event.delete;

import boot.action.ActionQueue;
import boot.action.EntityDeleteAction;
import boot.metamodel.MetaModel;

public class DefaultDeleteEventListener implements DeleteEventListener {

    private final MetaModel metaModel;
    private final ActionQueue actionQueue;

    public DefaultDeleteEventListener(MetaModel metaModel, ActionQueue actionQueue) {
        this.metaModel = metaModel;
        this.actionQueue = actionQueue;
    }

    @Override
    public <T> void onDelete(DeleteEvent<T> event) {
        EntityDeleteAction<T> deleteAction = new EntityDeleteAction<>(event.getEntity(), metaModel.getEntityPersister(event.getClazz()));
        actionQueue.addAction(deleteAction);
    }
}
