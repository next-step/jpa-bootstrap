package persistence.sql.context;

public interface EntityPersister {

    Object insert(Object entity);

    Object insert(Object entity, Object parentEntity);

    void update(Object entity, Object snapshotEntity);

    void delete(Object entity);
}
