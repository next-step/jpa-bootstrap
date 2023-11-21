package hibernate.event.merge;

import hibernate.action.ActionQueue;
import hibernate.action.InsertUpdateActionQueue;
import hibernate.entity.meta.column.EntityColumn;

import java.util.Map;

public class MergeEvent<T> {

    private final InsertUpdateActionQueue actionQueue;
    private final Class<T> clazz;
    private final Object entityId;
    private final Map<EntityColumn, Object> changeColumns;

    public MergeEvent(final ActionQueue actionQueue, final Class<T> clazz, final Object entityId, final Map<EntityColumn, Object> changeColumns) {
        this.actionQueue = actionQueue;
        this.clazz = clazz;
        this.entityId = entityId;
        this.changeColumns = changeColumns;
    }

    public InsertUpdateActionQueue getActionQueue() {
        return actionQueue;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public Object getEntityId() {
        return entityId;
    }

    public Map<EntityColumn, Object> getChangeColumns() {
        return changeColumns;
    }
}
