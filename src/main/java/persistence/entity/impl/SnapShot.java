package persistence.entity.impl;

import persistence.sql.schema.meta.EntityClassMappingMeta;
import persistence.sql.schema.meta.EntityObjectMappingMeta;

public class SnapShot {

    private final EntityObjectMappingMeta snapShotObjectMappingMeta;

    public SnapShot(Object snapShotEntity, EntityClassMappingMeta entityClassMappingMeta) {
        this.snapShotObjectMappingMeta = EntityObjectMappingMeta.of(snapShotEntity, entityClassMappingMeta);
    }

    public boolean isSameWith(Object entity, EntityClassMappingMeta entityClassMappingMeta) {
        EntityObjectMappingMeta targetObjectMappingMeta = EntityObjectMappingMeta.of(entity, entityClassMappingMeta);
        return this.snapShotObjectMappingMeta.getDifferMetaEntryList(targetObjectMappingMeta).size() == 0;
    }
}
