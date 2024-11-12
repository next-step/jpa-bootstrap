package persistence.event.persist;

import persistence.action.EntityCollectionInsertAction;
import persistence.entity.CollectionPersister;
import persistence.entity.EntityPersister;
import persistence.event.EventSource;

public class CollectionPersistEventListener implements PersistEventListener {

    @Override
    public void onPersist(PersistEvent event) {
        final EventSource source = event.getSession();
        final Object entity = event.getEntity();
        final EntityPersister persister = source.findEntityPersister(entity.getClass());

        persister.getCollectionAssociations().forEach(association -> {
            final CollectionPersister collectionPersister = source.getMetamodel().findCollectionPersister(association);

            source.getActionQueue().addAction(
                    new EntityCollectionInsertAction(event.getSession(), entity, collectionPersister, association)
            );
        });
    }
}
