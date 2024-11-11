package persistence.event;

import persistence.entity.CollectionPersister;
import persistence.entity.EntityEntry;
import persistence.entity.EntityPersister;

import java.util.Collection;

public class CollectionPersistEventListener extends AbstractPersistEventListener{

    @Override
    public void onPersist(PersistEvent event) {
        final EventSource source = event.getSession();
        final Object entity = event.getEntity();
        final EntityPersister persister = source.getEntityPersister(entity.getClass());

        persister.getCollectionAssociations().forEach(association -> {
            final CollectionPersister collectionPersister = source.getMetamodel().findCollectionPersister(association);
            final Collection<Object> childEntities = collectionPersister.insertCollection(entity, association);

            childEntities.forEach(child -> {
                final EntityPersister childPersister = source.getMetamodel().findEntityPersister(child.getClass());
                final EntityEntry childEntry = EntityEntry.inSaving();
                managePersistedEntity(source, childPersister, child, childEntry);
            });
        });

    }
}
