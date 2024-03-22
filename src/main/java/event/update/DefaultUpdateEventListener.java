package event.update;

import boot.action.ActionQueue;
import boot.action.EntityUpdateAction;
import boot.metamodel.MetaModel;

public class DefaultUpdateEventListener implements UpdateEventListener {

    private final MetaModel metaModel;
    private final ActionQueue actionQueue;

    public DefaultUpdateEventListener(MetaModel metaModel, ActionQueue actionQueue) {
        this.metaModel = metaModel;
        this.actionQueue = actionQueue;
    }

    @Override
    public <T> void onUpdate(UpdateEvent<T> event) {
        EntityUpdateAction<T> updateAction = new EntityUpdateAction<>(metaModel.getEntityPersister(event.getClazz()), event.getEntity());
        actionQueue.addAction(updateAction);
    }
}
