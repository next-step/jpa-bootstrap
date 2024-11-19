package event.impl;

import event.Event;
import persistence.sql.clause.Clause;
import persistence.sql.dml.MetadataLoader;

public class SaveOrUpdateEvent implements Event {
    private final Object entity;
    private final String entityName;
    private final Object entityId;

    public SaveOrUpdateEvent(Object entity, String entityName, Object entityId) {
        this.entity = entity;
        this.entityName = entityName;
        this.entityId = entityId;
    }

    public static SaveOrUpdateEvent create(Object entity, MetadataLoader<?> metadataLoader) {
        Object primaryKey = Clause.extractValue(metadataLoader.getPrimaryKeyField(), entity);

        return new SaveOrUpdateEvent(entity, metadataLoader.getEntityName(), primaryKey);

    }

    @Override
    public Object getEntity() {
        return entity;
    }

    @Override
    public String getEntityName() {
        return entityName;
    }

    @Override
    public Object getEntityId() {
        return entityId;
    }
}
