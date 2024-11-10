package persistence.bootstrap;

import jdbc.JdbcTemplate;
import persistence.entity.CollectionLoader;
import persistence.meta.EntityTable;
import persistence.sql.dml.DmlQueries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionLoaderBinder {
    private final Map<String, CollectionLoader> collectionLoaderRegistry = new HashMap<>();

    public CollectionLoaderBinder(List<Class<?>> entityTypes, EntityTableBinder entityTableBinder,
                                  JdbcTemplate jdbcTemplate, DmlQueries dmlQueries) {
        for (Class<?> entityType : entityTypes) {
            final EntityTable entityTable = entityTableBinder.getEntityTable(entityType);
            if (entityTable.isOneToMany()) {
                final EntityTable childEntityTable = new EntityTable(entityTable.getAssociationColumnType());
                final CollectionLoader collectionLoader =
                        new CollectionLoader(childEntityTable, jdbcTemplate, dmlQueries.getSelectQuery());

                final String collectionKey = getKey(entityType, entityTable.getAssociationColumnName());
                collectionLoaderRegistry.put(collectionKey, collectionLoader);
            }
        }
    }

    public CollectionLoader getCollectionLoader(Class<?> entityType, String columnName) {
        return collectionLoaderRegistry.get(getKey(entityType, columnName));
    }

    private String getKey(Class<?> entityType, String columnName) {
        return "%s.%s".formatted(entityType.getName(), columnName);
    }
}
