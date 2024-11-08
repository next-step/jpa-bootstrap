package persistence.entity;

import persistence.sql.definition.TableDefinition;

import java.util.HashMap;
import java.util.Map;

public class StatefulPersistenceContext implements PersistenceContext {
    private final Map<EntityKey, Object> managedEntities = new HashMap<>();
    private final Map<EntityKey, EntitySnapshot> entitySnapshots = new HashMap<>();
    private final Map<EntityKey, EntityEntry> entityEntries = new HashMap<>();

    @Override
    public Object getEntity(EntityKey entityKey) {
        return managedEntities.get(entityKey);
    }

    @Override
    public EntityEntry getEntityEntry(EntityKey entityKey) {
        return entityEntries.get(entityKey);
    }

    @Override
    public EntitySnapshot getDatabaseSnapshot(EntityKey entityKey) {
        return entitySnapshots.get(entityKey);
    }

    @Override
    public void addEntity(EntityKey entityKey, Object entity) {
        managedEntities.put(entityKey, entity);
    }

    @Override
    public void addDatabaseSnapshot(EntityKey entityKey, Object entity, TableDefinition tableDefinition) {
        final EntitySnapshot entitySnapshot = new EntitySnapshot(entity, tableDefinition);
        entitySnapshots.put(entityKey, entitySnapshot);
    }

    @Override
    public void removeEntity(EntityKey entityKey) {
        managedEntities.remove(entityKey);
        entitySnapshots.remove(entityKey);
    }

    @Override
    public void addEntry(EntityKey entityKey, EntityEntry entityEntry) {
        entityEntries.put(entityKey, entityEntry);
    }

    @Override
    public void clear() {
        managedEntities.clear();
        entitySnapshots.clear();
        entityEntries.clear();
    }

}
