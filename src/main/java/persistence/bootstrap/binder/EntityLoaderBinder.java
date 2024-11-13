package persistence.bootstrap.binder;

import jdbc.JdbcTemplate;
import jdbc.mapper.RowMapper;
import persistence.entity.loader.CollectionLoader;
import persistence.entity.loader.EntityLoader;
import persistence.entity.proxy.ProxyFactory;
import persistence.meta.EntityTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityLoaderBinder {
    private final Map<String, EntityLoader> entityLoaderRegistry = new HashMap<>();

    public EntityLoaderBinder(List<Class<?>> entityTypes, EntityTableBinder entityTableBinder,
                              CollectionLoaderBinder collectionLoaderBinder, RowMapperBinder rowMapperBinder, JdbcTemplate jdbcTemplate,
                              ProxyFactory proxyFactory) {
        for (Class<?> entityType : entityTypes) {
            final EntityLoader entityLoader =
                    createEntityLoader(entityTableBinder, collectionLoaderBinder, rowMapperBinder, jdbcTemplate, proxyFactory, entityType);
            entityLoaderRegistry.put(entityType.getTypeName(), entityLoader);
        }
    }

    public EntityLoader getEntityLoader(Class<?> entityType) {
        return entityLoaderRegistry.get(entityType.getName());
    }

    private EntityLoader createEntityLoader(EntityTableBinder entityTableBinder, CollectionLoaderBinder collectionLoaderBinder,
                                            RowMapperBinder rowMapperBinder, JdbcTemplate jdbcTemplate,
                                            ProxyFactory proxyFactory, Class<?> entityType) {
        final EntityTable entityTable = entityTableBinder.getEntityTable(entityType);
        final RowMapper rowMapper = rowMapperBinder.getRowMapper(entityType);
        if (entityTable.getAssociationEntityColumn() == null) {
            return new EntityLoader(entityTable, EntityTable.EMPTY, jdbcTemplate, proxyFactory,
                    rowMapper, null);
        }

        final EntityTable childEntityTable = entityTableBinder.getEntityTable(entityTable.getAssociationColumnType());
        final CollectionLoader collectionLoader =
                collectionLoaderBinder.getCollectionLoader(entityType, entityTable.getAssociationColumnName());
        return new EntityLoader(entityTable, childEntityTable, jdbcTemplate, proxyFactory,
                rowMapper, collectionLoader);
    }

    public void clear() {
        entityLoaderRegistry.clear();
    }
}
