package persistence.meta;

public class EntityAssociation {
    private final Class<?> entityType;
    private final EntityTable parentEntityTable;

    public EntityAssociation(Class<?> entityType, EntityTable parentEntityTable) {
        this.entityType = entityType;
        this.parentEntityTable = parentEntityTable;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public EntityTable getParentEntityTable() {
        return parentEntityTable;
    }
}
