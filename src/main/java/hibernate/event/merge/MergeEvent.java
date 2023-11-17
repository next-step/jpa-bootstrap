package hibernate.event.merge;

import hibernate.entity.EntityPersister;
import hibernate.entity.meta.column.EntityColumn;
import hibernate.metamodel.MetaModel;

import java.util.Map;

public class MergeEvent<T> {

    private final EntityPersister<T> entityPersister;
    private final Object entityId;
    private final Map<EntityColumn, Object> changeColumns;

    public MergeEvent(final EntityPersister<T> entityPersister, final Object entityId, final Map<EntityColumn, Object> changeColumns) {
        this.entityPersister = entityPersister;
        this.entityId = entityId;
        this.changeColumns = changeColumns;
    }

    public static <T> MergeEvent<T> createEvent(final MetaModel metaModel, final Class<T> clazz, final Object entityId, final Map<EntityColumn, Object> changeColumns) {
        return new MergeEvent<>(metaModel.getEntityPersister(clazz), entityId, changeColumns);
    }

    public EntityPersister<T> getEntityPersister() {
        return entityPersister;
    }

    public Object getEntityId() {
        return entityId;
    }

    public Map<EntityColumn, Object> getChangeColumns() {
        return changeColumns;
    }
}
