package event.impl;

import event.Event;
import persistence.sql.dml.MetadataLoader;

public class LoadEvent implements Event {
    private final MetadataLoader<?> metadataLoader;
    private final Object entityId;
    private final Object foreignKey;
    private final MetadataLoader<?> foreignMetadataLoader;

    public LoadEvent(Object entityId, MetadataLoader<?> metadataLoader) {
        this(metadataLoader, entityId, null, null);
    }

    public LoadEvent(MetadataLoader<?> metadataLoader, Object entityId, Object foreignKey, MetadataLoader<?> foreignMetadataLoader) {
        this.metadataLoader = metadataLoader;
        this.entityId = entityId;
        this.foreignKey = foreignKey;
        this.foreignMetadataLoader = foreignMetadataLoader;
    }

    @Override
    public Object getEntity() {
        return null;
    }

    @Override
    public String getEntityName() {
        return metadataLoader.getEntityName();
    }

    @Override
    public Object getEntityId() {
        return entityId;
    }

    public MetadataLoader<?> getForeignMetadataLoader() {
        return foreignMetadataLoader;
    }

    public Object getForeignKey() {
        return foreignKey;
    }
}
