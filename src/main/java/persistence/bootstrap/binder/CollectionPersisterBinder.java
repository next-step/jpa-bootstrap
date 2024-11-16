package persistence.bootstrap.binder;

import jdbc.JdbcTemplate;
import persistence.entity.persister.CollectionPersister;
import persistence.meta.EntityTable;

import java.util.HashMap;
import java.util.Map;

public class CollectionPersisterBinder {
    private final Map<String, CollectionPersister> entityPersisterRegistry = new HashMap<>();

    public CollectionPersisterBinder(EntityBinder entityBinder, EntityTableBinder entityTableBinder,
                                     JdbcTemplate jdbcTemplate) {
        for (Class<?> entityType : entityBinder.getEntityTypes()) {
            final EntityTable entityTable = entityTableBinder.getEntityTable(entityType);
            if (!entityTable.isOneToMany()) {
                continue;
            }

            final EntityTable childEntityTable = entityTableBinder.getEntityTable(entityTable.getAssociationColumnType());
            final CollectionPersister collectionPersister =
                    new CollectionPersister(childEntityTable, entityTable, jdbcTemplate);

            final String collectionKey = getKey(entityType, entityTable.getAssociationColumnName());
            entityPersisterRegistry.put(collectionKey, collectionPersister);
        }
    }

    public CollectionPersister getCollectionPersister(Class<?> entityType, String columnName) {
        return entityPersisterRegistry.get(getKey(entityType, columnName));
    }

    public void clear() {
        entityPersisterRegistry.clear();
    }

    private String getKey(Class<?> entityType, String columnName) {
        return "%s.%s".formatted(entityType.getName(), columnName);
    }
}
