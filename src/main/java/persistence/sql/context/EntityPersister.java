package persistence.sql.context;

import persistence.sql.dml.MetadataLoader;

public interface EntityPersister<T> {

    T insert(Object entity);

    <P> T insert(Object entity, P parentEntity);

    void update(Object entity, Object snapshotEntity);

    void delete(Object entity);

    MetadataLoader<T> getMetadataLoader();

    Object getIdentifier(Object entity);

    boolean isIdentifierUnsaved(Object id);
}
