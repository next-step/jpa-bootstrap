package persistence.listener;

public class LoadEvent {
    private final Class<?> entityType;
    private final Object entityId;
    private Object loadedEntity;

    public LoadEvent(Class<?> entityType, Object entityId) {
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId.toString();
    }

    public Object getLoadedEntity() {
        return loadedEntity;
    }

    public void setLoadedEntity(Object loadedEntity) {
        this.loadedEntity = loadedEntity;
    }
}
