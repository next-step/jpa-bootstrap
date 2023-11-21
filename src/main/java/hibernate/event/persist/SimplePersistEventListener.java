package hibernate.event.persist;

import hibernate.action.EntityBasicInsertAction;
import hibernate.action.EntityIdentityInsertAction;
import hibernate.metamodel.MetaModel;

public class SimplePersistEventListener implements PersistEventListener {

    private final MetaModel metaModel;

    public SimplePersistEventListener(final MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    @Override
    public <T> void onPersist(final PersistEvent<T> event) {
        if (event.isIdentity()) {
            event.getActionQueue()
                    .addAction(new EntityIdentityInsertAction<>(metaModel.getEntityPersister(event.getClazz()), event.getEntity(), event.getEntityId()));
            return;
        }
        event.getActionQueue()
                .addAction(new EntityBasicInsertAction<>(metaModel.getEntityPersister(event.getClazz()), event.getEntity()));
    }
}
