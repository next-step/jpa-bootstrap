package persistence.entity.event;


import persistence.entity.event.action.ActionType;
import persistence.sql.meta.Table;

public class EntityEvent<T> {
    private T entity;
    private final Class<T> entityClass;
    private Object entityId;
    private EventType eventType;

    public EntityEvent(Class<T> entityClass, EventType eventType) {
        this.entityClass = entityClass;
        this.eventType = eventType;
    }

    public EntityEvent(Class<T> entity, Object entityId, EventType eventType) {
        this(entity, eventType);
        this.entityId = entityId;
    }

    public EntityEvent(T entity, EventType eventType) {
        Table table = Table.getInstance(entity.getClass());
        this.entityClass = (Class<T>) entity.getClass();
        this.entityId = table.getIdValue(entity);
        this.entity = entity;
        this.eventType = eventType;
    }

    public T getEntity() {
        return entity;
    }

    public Object getEntityId() {
        return entityId;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public ActionType getActionType() {
        return eventType.getActionType();
    }

    public EventType getEventType() {
        return eventType;
    }
}
