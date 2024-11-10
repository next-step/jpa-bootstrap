package persistence.bootstrap;

import jdbc.JdbcTemplate;
import persistence.entity.CollectionLoader;
import persistence.entity.CollectionPersister;
import persistence.entity.EntityLoader;
import persistence.entity.EntityPersister;
import persistence.entity.proxy.ProxyFactory;
import persistence.meta.EntityTable;
import persistence.sql.dml.DeleteQuery;
import persistence.sql.dml.InsertQuery;
import persistence.sql.dml.SelectQuery;
import persistence.sql.dml.UpdateQuery;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Metamodel {
    private static final String INIT_FAILED_MESSAGE = "Metamodel 초기화에 실패하였습니다.";

    private final Map<String, EntityTable> entityTableRegistry = new ConcurrentHashMap<>();
    private final Map<String, EntityPersister> entityPersisterRegistry = new ConcurrentHashMap<>();
    private final Map<String, EntityLoader> entityLoaderRegistry = new ConcurrentHashMap<>();
    private final Map<String, CollectionPersister> collectionPersisterRegistry = new ConcurrentHashMap<>();
    private final Map<String, CollectionLoader> collectionLoaderRegistry = new ConcurrentHashMap<>();

    public Metamodel(JdbcTemplate jdbcTemplate, String... basePackages) {
        final EntityHolder entityHolder = new EntityHolder(basePackages);
        initRegistry(entityHolder.getEntityTypes(), jdbcTemplate);
    }

    private void initRegistry(List<Class<?>> entities, JdbcTemplate jdbcTemplate) {
        final SelectQuery selectQuery = new SelectQuery();
        final InsertQuery insertQuery = new InsertQuery();
        final UpdateQuery updateQuery = new UpdateQuery();
        final DeleteQuery deleteQuery = new DeleteQuery();
        final ProxyFactory proxyFactory = new ProxyFactory();

        for (Class<?> entity : entities) {
            final EntityTable entityTable = new EntityTable(entity);
            entityTableRegistry.put(entity.getTypeName(), entityTable);

            CollectionLoader collectionLoader = null;
            if (entityTable.isOneToMany()) {
                final String collectionKey = getKey(entity, entityTable.getAssociationColumnName());
                final EntityTable childEntityTable = new EntityTable(entityTable.getAssociationColumnType());
                collectionLoader = new CollectionLoader(childEntityTable, jdbcTemplate, selectQuery);

                collectionPersisterRegistry.put(
                        collectionKey, new CollectionPersister(childEntityTable, entityTable, jdbcTemplate, insertQuery));
                collectionLoaderRegistry.put(collectionKey, collectionLoader);
            }

            entityPersisterRegistry.put(entity.getTypeName(),
                    new EntityPersister(entityTable, jdbcTemplate, insertQuery, updateQuery, deleteQuery));

            if (Objects.nonNull(collectionLoader)) {
                entityLoaderRegistry.put(entity.getTypeName(),
                        new EntityLoader(entityTable, jdbcTemplate, selectQuery, proxyFactory, collectionLoader));
                continue;
            }
            entityLoaderRegistry.put(entity.getTypeName(),
                    new EntityLoader(entityTable, jdbcTemplate, selectQuery, proxyFactory, null));
        }
    }

    public EntityTable getEntityTable(Class<?> entityType) {
        return entityTableRegistry.get(entityType.getName());
    }

    public EntityLoader getEntityLoader(Class<?> entityType) {
        return entityLoaderRegistry.get(entityType.getName());
    }

    public EntityPersister getEntityPersister(Class<?> entityType) {
        return entityPersisterRegistry.get(entityType.getName());
    }

    public CollectionLoader getCollectionLoader(Class<?> entityType, String columnName) {
        return collectionLoaderRegistry.get(getKey(entityType, columnName));
    }

    public CollectionPersister getCollectionPersister(Class<?> entityType, String columnName) {
        return collectionPersisterRegistry.get(getKey(entityType, columnName));
    }

    private String getKey(Class<?> entityType, String columnName) {
        return "%s.%s".formatted(entityType.getName(), columnName);
    }
}
