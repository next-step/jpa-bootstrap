package hibernate.event.persist;

import hibernate.action.EntityBasicInsertAction;
import hibernate.action.EntityIdentityInsertAction;
import hibernate.action.InsertUpdateActionQueue;
import hibernate.metamodel.MetaModel;

public class SimplePersistEventListener implements PersistEventListener {

    private final MetaModel metaModel;
    private final InsertUpdateActionQueue actionQueue;

    public SimplePersistEventListener(final MetaModel metaModel, final InsertUpdateActionQueue actionQueue) {
        this.metaModel = metaModel;
        this.actionQueue = actionQueue;
    }

    @Override
    public <T> void onPersist(final PersistEvent<T> event) {
        if (event.isIdentity()) {
            actionQueue.addAction(new EntityIdentityInsertAction<>(metaModel.getEntityPersister(event.getClazz()), event.getEntity(), event.getEntityId()));
            return;
        }
        actionQueue.addAction(new EntityBasicInsertAction<>(metaModel.getEntityPersister(event.getClazz()), event.getEntity(), event.getEntityId()));
    }
}
