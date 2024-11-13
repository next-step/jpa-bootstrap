package persistence.bootstrap.binder;

import persistence.meta.EntityAssociation;
import persistence.meta.EntityColumn;
import persistence.meta.EntityTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EntityAssociationBinder {
    private final Map<String, EntityAssociation> entityAssociationRegistry = new HashMap<>();

    public EntityAssociationBinder(EntityTableBinder entityTableBinder) {
        final List<EntityTable> entityTables = entityTableBinder.getAllEntityTables();

        for (EntityTable entityTable : entityTables) {
            final EntityColumn associationEntityColumn = entityTable.getAssociationEntityColumn();
            if (Objects.isNull(associationEntityColumn)) {
                continue;
            }

            final Class<?> associationColumnType = entityTable.getAssociationColumnType();
            final EntityAssociation entityAssociation =
                    new EntityAssociation(associationColumnType, entityTable);
            entityAssociationRegistry.put(associationColumnType.getName(), entityAssociation);
        }
    }

    public EntityAssociation getEntityAssociation(Class<?> entityType) {
        return entityAssociationRegistry.get(entityType.getName());
    }

    public void clear() {
        entityAssociationRegistry.clear();
    }
}
