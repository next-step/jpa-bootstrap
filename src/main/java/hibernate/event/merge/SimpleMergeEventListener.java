package hibernate.event.merge;

import hibernate.action.EntityIdentityInsertAction;
import hibernate.action.EntityUpdateAction;
import hibernate.action.InsertUpdateActionQueue;
import hibernate.metamodel.MetaModel;

public class SimpleMergeEventListener implements MergeEventListener {

    private final MetaModel metaModel;
    private final InsertUpdateActionQueue actionQueue;

    public SimpleMergeEventListener(final MetaModel metaModel, final InsertUpdateActionQueue actionQueue) {
        this.metaModel = metaModel;
        this.actionQueue = actionQueue;
    }

    @Override
    public <T> void onMerge(final MergeEvent<T> event) {
        if (event.getChangeColumns() == null) {
            actionQueue.addAction(new EntityIdentityInsertAction<>(metaModel.getEntityPersister(event.getClazz()), event.getChangedEntity(), event.getEntityColumnId()));
            return;
        }
        actionQueue.addAction(new EntityUpdateAction<>(metaModel.getEntityPersister(event.getClazz()), event.getEntityId(), event.getChangeColumns()));
    }
}
