package persistence.entity.event.delete;

import bootstrap.MetaModel;
import persistence.entity.event.PersistEvent;
import persistence.entity.event.PersistEventListener;
import persistence.entity.event.action.ActionQueue;
import persistence.entity.event.action.EntityDeleteAction;
import persistence.entity.event.save.SaveEvent;

public class DeleteEventListener implements PersistEventListener {

    private final MetaModel metaModel;
    private final ActionQueue actionQueue;

    public DeleteEventListener(MetaModel metaModel, ActionQueue actionQueue) {
        this.metaModel = metaModel;
        this.actionQueue = actionQueue;
    }

    @Override
    public <T, ID> void fireEvent(PersistEvent<T, ID> event) {
        actionQueue.addAction(
                new EntityDeleteAction<>(
                        event.getEntity(),
                        event.getId(),
                        metaModel.getEntityPersister(event.getEntity().getClass()))
        );
    }
}
