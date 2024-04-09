package persistence.entity.context.cache;

import persistence.model.PersistentClass;

import java.util.HashMap;
import java.util.Map;

public class EntitySnapshots {

    private final Map<EntityKey, EntitySnapshot> entitySnapshots = new HashMap<>();

    public void add(final EntityKey entityKey, final Object entity, final PersistentClass<?> persistentClass) {
        entitySnapshots.put(entityKey, new EntitySnapshot(persistentClass, entity));
    }

    public EntitySnapshot get(final EntityKey entityKey, final Object entity, final PersistentClass<?> persistentClass) {
        return entitySnapshots.computeIfAbsent(entityKey, key -> new EntitySnapshot(persistentClass, entity));
    }

    public void remove(final EntityKey entityKey) {
        entitySnapshots.remove(entityKey);
    }

}
