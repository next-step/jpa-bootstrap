package persistence.action;

import persistence.entity.CollectionPersister;
import persistence.entity.EntityEntry;
import persistence.entity.EntityPersister;
import persistence.event.EventSource;
import persistence.sql.definition.TableAssociationDefinition;

import java.util.Collection;

public class EntityCollectionInsertAction extends BaseInsertAction {
    private final EventSource source;
    private final Object parentEntity;
    private final CollectionPersister collectionPersister;
    private final TableAssociationDefinition association;

    public EntityCollectionInsertAction(EventSource source,
                                        Object parentEntity,
                                        CollectionPersister collectionPersister,
                                        TableAssociationDefinition association) {
        this.source = source;
        this.parentEntity = parentEntity;
        this.collectionPersister = collectionPersister;
        this.association = association;
    }

    public void execute() {
        final Collection<Object> childEntities = collectionPersister.insertCollection(parentEntity, association);

        childEntities.forEach(child -> {
            final EntityPersister childPersister = source.findEntityPersister(child.getClass());
            final EntityEntry childEntry = EntityEntry.inSaving();
            managePersistedEntity(source, childPersister, child, childEntry);
        });
    }

    public Object getParentEntity() {
        return parentEntity;
    }
}
