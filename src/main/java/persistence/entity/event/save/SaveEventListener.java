package persistence.entity.event.save;

import bootstrap.MetaModel;
import persistence.entity.EntityPersister;
import persistence.entity.event.PersistEvent;
import persistence.entity.event.PersistEventListener;
import persistence.entity.event.action.ActionQueue;
import persistence.entity.event.action.EntityDeleteAction;
import persistence.entity.event.action.EntityInsertAction;
import persistence.entity.event.load.LoadEvent;

public class SaveEventListener implements PersistEventListener {

    private final MetaModel metaModel;
    private final ActionQueue actionQueue;

    public SaveEventListener(MetaModel metaModel, ActionQueue actionQueue) {
        this.metaModel = metaModel;
        this.actionQueue = actionQueue;
    }

    @Override
    public <T, ID> void fireEvent(PersistEvent<T, ID> event) {
        actionQueue.addAction(
                new EntityInsertAction<>(
                        event.getEntity(),
                        metaModel.getEntityPersister(event.getEntity().getClass()))
        );
    }
}
