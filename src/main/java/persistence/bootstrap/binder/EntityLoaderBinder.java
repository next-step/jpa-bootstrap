package persistence.bootstrap.binder;

import jdbc.JdbcTemplate;
import jdbc.mapper.RowMapper;
import persistence.entity.loader.CollectionLoader;
import persistence.entity.loader.EntityLoader;
import persistence.entity.proxy.ProxyFactory;
import persistence.meta.EntityTable;

import java.util.HashMap;
import java.util.Map;

public class EntityLoaderBinder {
    private final Map<String, EntityLoader> entityLoaderRegistry = new HashMap<>();

    public EntityLoaderBinder(EntityBinder entityBinder, EntityTableBinder entityTableBinder, CollectionLoaderBinder collectionLoaderBinder,
                              RowMapperBinder rowMapperBinder, JdbcTemplate jdbcTemplate) {
        for (Class<?> entityType : entityBinder.getEntityTypes()) {
            final EntityLoader entityLoader = createEntityLoader(entityTableBinder, collectionLoaderBinder, rowMapperBinder, jdbcTemplate, entityType);
            entityLoaderRegistry.put(entityType.getTypeName(), entityLoader);
        }
    }

    public EntityLoader getEntityLoader(Class<?> entityType) {
        return entityLoaderRegistry.get(entityType.getName());
    }

    private EntityLoader createEntityLoader(EntityTableBinder entityTableBinder, CollectionLoaderBinder collectionLoaderBinder,
                                            RowMapperBinder rowMapperBinder, JdbcTemplate jdbcTemplate, Class<?> entityType) {
        final EntityTable entityTable = entityTableBinder.getEntityTable(entityType);
        final RowMapper rowMapper = rowMapperBinder.getRowMapper(entityType);
        if (entityTable.getAssociationEntityColumn() == null) {
            return new EntityLoader(entityTable, EntityTable.EMPTY, jdbcTemplate, ProxyFactory.getInstance(), rowMapper, null);
        }

        final EntityTable childEntityTable = entityTableBinder.getEntityTable(entityTable.getAssociationColumnType());
        final CollectionLoader collectionLoader =
                collectionLoaderBinder.getCollectionLoader(entityType, entityTable.getAssociationColumnName());
        return new EntityLoader(entityTable, childEntityTable, jdbcTemplate, ProxyFactory.getInstance(), rowMapper, collectionLoader);
    }

    public void clear() {
        entityLoaderRegistry.clear();
    }
}
