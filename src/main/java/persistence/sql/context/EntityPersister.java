package persistence.sql.context;

import persistence.sql.dml.MetadataLoader;

public interface EntityPersister {

    Object insert(Object entity);

    Object insert(Object entity, Object parentEntity);

    void update(Object entity, Object snapshotEntity);

    void delete(Object entity);

    MetadataLoader<?> getMetadataLoader();
}
