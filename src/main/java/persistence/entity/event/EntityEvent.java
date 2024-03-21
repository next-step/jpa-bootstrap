package persistence.entity.event;


import persistence.sql.meta.Table;

public class EntityEvent<T> {
    private T entity;
    private final Class<T> entityClass;
    private Object entityId;

    public EntityEvent(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public EntityEvent(Class<T> entity, Object entityId) {
        this.entityClass = entity;
        this.entityId = entityId;
    }

    public EntityEvent(T entity) {
        Table table = Table.getInstance(entity.getClass());
        this.entityClass = (Class<T>) entity.getClass();
        this.entityId = table.getIdValue(entity);
        this.entity = entity;
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
}
