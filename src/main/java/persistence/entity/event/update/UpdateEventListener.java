package persistence.entity.event.update;

import bootstrap.MetaModel;
import persistence.entity.event.PersistEvent;
import persistence.entity.event.PersistEventListener;
import persistence.entity.event.action.ActionQueue;
import persistence.entity.event.action.EntityUpdateAction;
import persistence.entity.event.save.SaveEvent;

public class UpdateEventListener implements PersistEventListener {
    private final MetaModel metaModel;
    private final ActionQueue actionQueue;

    public UpdateEventListener(MetaModel metaModel, ActionQueue actionQueue) {
        this.metaModel = metaModel;
        this.actionQueue = actionQueue;
    }

    @Override
    public <T, ID> void fireEvent(PersistEvent<T, ID> event) {
        actionQueue.addAction(
                new EntityUpdateAction<>(
                        event.getEntity(),
                        event.getId(),
                        metaModel.getEntityPersister(event.getEntity().getClass())));
    }
}
