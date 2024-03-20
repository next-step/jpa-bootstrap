package event.save;

import boot.action.ActionQueue;
import boot.action.EntityInsertAction;
import boot.metamodel.MetaModel;

public class DefaultSaveEventListener implements SaveEventListener {

    private final MetaModel metaModel;
    private final ActionQueue actionQueue;

    public DefaultSaveEventListener(MetaModel metaModel, ActionQueue actionQueue) {
        this.metaModel = metaModel;
        this.actionQueue = actionQueue;
    }

    @Override
    public <T> void onSave(SaveEvent<T> event) {
        T entity = event.getEntity();
        EntityInsertAction<T> insertAction = new EntityInsertAction<>(entity, metaModel.getEntityPersister(event.getClazz()), metaModel.getEntityMetaFrom(entity));
        actionQueue.addAction(insertAction);
    }
}
