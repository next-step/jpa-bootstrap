package persistence.entity;

import jdbc.DefaultRowMapper;
import jdbc.JdbcTemplate;
import persistence.entity.proxy.ProxyFactory;
import persistence.meta.EntityTable;
import persistence.sql.dml.SelectQuery;

import java.lang.reflect.Field;
import java.util.List;

public class EntityLoader {
    private static final String PROXY_CREATION_FAILED_MESSAGE = "프록시 생성에 실패하였습니다.";

    private final JdbcTemplate jdbcTemplate;
    private final SelectQuery selectQuery;
    private final ProxyFactory proxyFactory;

    public EntityLoader(JdbcTemplate jdbcTemplate, SelectQuery selectQuery, ProxyFactory proxyFactory) {
        this.jdbcTemplate = jdbcTemplate;
        this.selectQuery = selectQuery;
        this.proxyFactory = proxyFactory;
    }

    public <T> T load(Class<T> entityType, Object id) {
        final String sql = selectQuery.findById(entityType, id);
        final T entity = jdbcTemplate.queryForObject(sql, new DefaultRowMapper<>(entityType));

        final EntityTable entityTable = new EntityTable(entityType);
        if (entityTable.isOneToManyAssociation() && !entityTable.isEager()) {
            setProxy(entityTable, entity);
        }

        return entity;
    }

    private void setProxy(EntityTable entityTable, Object entity) {
        final CollectionLoader collectionLoader = new CollectionLoader(jdbcTemplate, selectQuery);
        final List<?> proxy = proxyFactory.createProxy(
                entity, new LazyLoader<>(entityTable.getJoinColumnType(), entity, collectionLoader)
        );

        try {
            final Field associationField = entityTable.getAssociationField();
            associationField.setAccessible(true);
            associationField.set(entity, proxy);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(PROXY_CREATION_FAILED_MESSAGE, e);
        }
    }
}
