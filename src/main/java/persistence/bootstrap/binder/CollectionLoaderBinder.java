package persistence.bootstrap.binder;

import jdbc.JdbcTemplate;
import jdbc.mapper.RowMapper;
import persistence.entity.loader.CollectionLoader;
import persistence.meta.EntityTable;

import java.util.HashMap;
import java.util.Map;

public class CollectionLoaderBinder {
    private final Map<String, CollectionLoader> collectionLoaderRegistry = new HashMap<>();

    public CollectionLoaderBinder(EntityBinder entityBinder, EntityTableBinder entityTableBinder,
                                  RowMapperBinder rowMapperBinder, JdbcTemplate jdbcTemplate) {
        for (Class<?> entityType : entityBinder.getEntityTypes()) {
            final EntityTable entityTable = entityTableBinder.getEntityTable(entityType);
            if (!entityTable.isOneToMany()) {
                continue;
            }

            final Class<?> associationColumnType = entityTable.getAssociationColumnType();
            final EntityTable childEntityTable = entityTableBinder.getEntityTable(associationColumnType);
            final RowMapper rowMapper = rowMapperBinder.getRowMapper(associationColumnType);
            final CollectionLoader collectionLoader =
                    new CollectionLoader(childEntityTable, jdbcTemplate, rowMapper);

            final String collectionKey = getKey(entityType, entityTable.getAssociationColumnName());
            collectionLoaderRegistry.put(collectionKey, collectionLoader);
        }
    }

    public CollectionLoader getCollectionLoader(Class<?> entityType, String columnName) {
        return collectionLoaderRegistry.get(getKey(entityType, columnName));
    }

    private String getKey(Class<?> entityType, String columnName) {
        return "%s.%s".formatted(entityType.getName(), columnName);
    }
}
