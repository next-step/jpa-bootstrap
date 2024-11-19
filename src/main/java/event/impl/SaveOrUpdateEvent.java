package event.impl;

import event.Event;

public class SaveOrUpdateEvent implements Event {
    private Object entity;
    private String entityName;
    private Object entityId;

    public SaveOrUpdateEvent(Object entity, String entityName, Object entityId) {
        this.entity = entity;
        this.entityName = entityName;
        this.entityId = entityId;
    }

    public static

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
