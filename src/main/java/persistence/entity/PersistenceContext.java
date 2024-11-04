package persistence.entity;

import persistence.sql.definition.TableDefinition;

public interface PersistenceContext {

    Object getEntity(EntityKey entityKey);

    EntityEntry getEntityEntry(EntityKey entityKey);

    EntitySnapshot getDatabaseSnapshot(EntityKey entityKey);

    void addEntity(EntityKey entityKey, Object entity);

    void addDatabaseSnapshot(EntityKey entityKey, Object entity, TableDefinition tableDefinition);

    void removeEntity(EntityKey entityKey);

    void addEntry(EntityKey entityKey, EntityEntry entityEntry);

    void clear();
}
