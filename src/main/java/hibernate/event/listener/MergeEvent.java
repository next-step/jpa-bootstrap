package hibernate.event.listener;

import hibernate.entity.EntityPersister;
import hibernate.entity.meta.column.EntityColumn;
import hibernate.metamodel.MetaModel;

import java.util.Map;

public class MergeEvent {

    private final EntityPersister<?> entityPersister;
    private final Object entityId;
    private final Map<EntityColumn, Object> changeColumns;

    public MergeEvent(final EntityPersister<?> entityPersister, final Object entityId, final Map<EntityColumn, Object> changeColumns) {
        this.entityPersister = entityPersister;
        this.entityId = entityId;
        this.changeColumns = changeColumns;
    }

    public static MergeEvent createEvent(final MetaModel metaModel, final Class<?> clazz, final Object entityId, final Map<EntityColumn, Object> changeColumns) {
        return new MergeEvent(metaModel.getEntityPersister(clazz), entityId, changeColumns);
    }

    public EntityPersister<?> getEntityPersister() {
        return entityPersister;
    }

    public Object getEntityId() {
        return entityId;
    }

    public Map<EntityColumn, Object> getChangeColumns() {
        return changeColumns;
    }
}
