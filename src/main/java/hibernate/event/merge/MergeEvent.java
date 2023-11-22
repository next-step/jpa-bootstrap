package hibernate.event.merge;

import hibernate.entity.meta.column.EntityColumn;

import java.util.Map;

public class MergeEvent<T> {

    private final Class<T> clazz;
    private final Object entityId;
    private final EntityColumn entityColumnId;
    private final Map<EntityColumn, Object> changeColumns;
    private final T changedEntity;

    private MergeEvent(final Class<T> clazz, final Object entityId, final EntityColumn entityColumnId, final Map<EntityColumn, Object> changeColumns, final T changedEntity) {
        this.clazz = clazz;
        this.entityId = entityId;
        this.entityColumnId = entityColumnId;
        this.changeColumns = changeColumns;
        this.changedEntity = changedEntity;
    }

    public static <T> MergeEvent<T> createEvent(final T originalEntity, final EntityColumn entityColumnId, final Map<EntityColumn, Object> changeColumns) {
        Class<T> clazz = (Class<T>) originalEntity.getClass();
        return new MergeEvent<>(
                clazz,
                entityColumnId.getFieldValue(originalEntity),
                entityColumnId,
                changeColumns,
                originalEntity
        );
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public Object getEntityId() {
        return entityId;
    }

    public EntityColumn getEntityColumnId() {
        return entityColumnId;
    }

    public Map<EntityColumn, Object> getChangeColumns() {
        return changeColumns;
    }

    public T getChangedEntity() {
        return changedEntity;
    }
}
