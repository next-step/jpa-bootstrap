package event.impl;

import event.Event;
import persistence.sql.clause.Clause;
import persistence.sql.dml.MetadataLoader;

public class DeleteEvent implements Event {
    private final Object entity;
    private final String entityName;
    private final Object entityId;

    public DeleteEvent(Object entity, String entityName, Object entityId) {
        this.entity = entity;
        this.entityName = entityName;
        this.entityId = entityId;
    }

    public static DeleteEvent create(Object entity, MetadataLoader<?> metadataLoader) {
        Object primaryKey = Clause.extractValue(metadataLoader.getPrimaryKeyField(), entity);

        return new DeleteEvent(entity, metadataLoader.getEntityName(), primaryKey);
    }

    @Override
    public Object entity() {
        return entity;
    }

    @Override
    public String entityName() {
        return entityName;
    }

    @Override
    public Object entityId() {
        return entityId;
    }
}
