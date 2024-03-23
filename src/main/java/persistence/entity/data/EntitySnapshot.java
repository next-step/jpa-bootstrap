package persistence.entity.data;

import database.mapping.column.EntityColumn;
import database.sql.dml.part.ValueMap;
import persistence.entity.context.PersistentClass;

import java.util.HashMap;
import java.util.Map;

public class EntitySnapshot {
    private final Map<String, Object> snapshot;

    private EntitySnapshot() {
        this.snapshot = new HashMap<>();
    }

    public static <T> EntitySnapshot of(PersistentClass<T> persistentClass, T entity) {
        EntitySnapshot newSnapshot = new EntitySnapshot();

        if (entity == null) {
            return newSnapshot;
        }

        for (EntityColumn column : persistentClass.getGeneralColumns()) {
            String key = column.getColumnName();
            Object value = column.getValue(entity);
            newSnapshot.snapshot.put(key, value);
        }
        return newSnapshot;
    }

    public ValueMap diff(EntitySnapshot newEntitySnapshot) {
        Map<String, Object> changes = new HashMap<>();

        for (String key : newEntitySnapshot.snapshot.keySet()) {
            Object oldValue = snapshot.get(key);
            Object newValue = newEntitySnapshot.snapshot.get(key);

            if (isDiffer(oldValue, newValue)) {
                changes.put(key, newValue);
            }
        }
        return ValueMap.from(changes);
    }

    private boolean isDiffer(Object oldValue, Object newValue) {
        if (oldValue == null) {
            return newValue != null;
        }
        return !oldValue.equals(newValue);
    }
}
