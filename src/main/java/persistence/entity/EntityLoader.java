package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import persistence.entity.proxy.ProxyFactory;
import persistence.meta.EntityTable;
import persistence.sql.dml.SelectQuery;

import java.lang.reflect.Field;
import java.util.List;

public class EntityLoader {
    private static final String PROXY_CREATION_FAILED_MESSAGE = "프록시 생성에 실패하였습니다.";

    private final EntityTable entityTable;
    private final EntityTable childEntityTable;
    private final JdbcTemplate jdbcTemplate;
    private final SelectQuery selectQuery;
    private final ProxyFactory proxyFactory;
    private final RowMapper rowMapper;
    private final CollectionLoader collectionLoader;

    public EntityLoader(EntityTable entityTable, EntityTable childEntityTable, JdbcTemplate jdbcTemplate, SelectQuery selectQuery,
                        ProxyFactory proxyFactory, RowMapper rowMapper,
                        CollectionLoader collectionLoader) {
        this.entityTable = entityTable;
        this.childEntityTable = childEntityTable;
        this.jdbcTemplate = jdbcTemplate;
        this.selectQuery = selectQuery;
        this.proxyFactory = proxyFactory;
        this.rowMapper = rowMapper;
        this.collectionLoader = collectionLoader;
    }

    public <T> T load(Object id) {
        final String sql = getSql(id);
        final T entity = jdbcTemplate.queryForObject(sql, rowMapper);

        if (entityTable.isOneToMany() && !entityTable.isEager()) {
            setProxy(entityTable, entity);
        }
        return entity;
    }

    private String getSql(Object id) {
        if (entityTable.isOneToMany() && entityTable.isEager()) {
            return selectQuery.findById(entityTable, childEntityTable, id);
        }
        return selectQuery.findById(entityTable, id);
    }

    private void setProxy(EntityTable entityTable, Object entity) {
        final List<?> proxy = proxyFactory.createProxy(entity, new LazyLoader(entityTable, collectionLoader));

        try {
            final Field associationField = entityTable.getAssociationField();
            associationField.setAccessible(true);
            associationField.set(entity, proxy);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(PROXY_CREATION_FAILED_MESSAGE, e);
        }
    }
}
