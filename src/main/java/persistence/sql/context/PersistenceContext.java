package persistence.sql.context;

import boot.MetaModel;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.entity.CollectionEntry;
import persistence.sql.entity.EntityEntry;
import persistence.sql.entity.data.Status;

public interface PersistenceContext {

    EntityEntry addEntry(Object entity, Status status, EntityPersister<?> entityPersister);

    EntityEntry addLoadingEntry(Object primaryKey, MetadataLoader<?> metadataLoader);

    <ID> EntityEntry getEntry(Class<?> entityType, ID id);

    CollectionEntry getCollectionEntry(CollectionKeyHolder key);

    CollectionEntry addCollectionEntry(CollectionKeyHolder key, CollectionEntry collectionEntry);

    <ID> void deleteEntry(Object entity, ID id);

    void cleanup();

    void dirtyCheck(MetaModel metaModel);
}
