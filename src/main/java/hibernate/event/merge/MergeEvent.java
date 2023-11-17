package hibernate.event.merge;

import hibernate.action.ActionQueue;
import hibernate.entity.EntityPersister;
import hibernate.entity.EntitySource;
import hibernate.entity.meta.column.EntityColumn;
import hibernate.event.AbstractEntityEvent;

import java.util.Map;

public class MergeEvent<T> extends AbstractEntityEvent {

    private final EntityPersister<T> entityPersister;
    private final Object entityId;
    private final Map<EntityColumn, Object> changeColumns;

    public MergeEvent(final ActionQueue actionQueue, final EntityPersister<T> entityPersister, final Object entityId, final Map<EntityColumn, Object> changeColumns) {
        super(actionQueue);
        this.entityPersister = entityPersister;
        this.entityId = entityId;
        this.changeColumns = changeColumns;
    }

    public static <T> MergeEvent<T> createEvent(final EntitySource entitySource, final Class<T> clazz, final Object entityId, final Map<EntityColumn, Object> changeColumns) {
        return new MergeEvent<>(
                entitySource.getActionQueue(),
                entitySource.getMetaModel().getEntityPersister(clazz),
                entityId,
                changeColumns
        );
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
