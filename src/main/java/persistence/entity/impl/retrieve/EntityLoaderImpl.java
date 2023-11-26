package persistence.entity.impl.retrieve;

import java.sql.Connection;
import jdbc.JdbcTemplate;
import persistence.entity.impl.EntityRowMapper;
import persistence.sql.dml.clause.operator.EqualOperator;
import persistence.sql.dml.clause.predicate.WherePredicate;
import persistence.sql.dml.statement.SelectStatementBuilder;
import persistence.sql.schema.meta.EntityClassMappingMeta;
import registry.EntityMetaRegistry;

public class EntityLoaderImpl implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final EntityMetaRegistry entityMetaRegistry;
    private final EntityCollectionLoader entityCollectionLoader;

    public EntityLoaderImpl(Connection connection, EntityMetaRegistry entityMetaRegistry) {
        this.entityMetaRegistry = entityMetaRegistry;
        this.jdbcTemplate = new JdbcTemplate(connection);
        this.entityCollectionLoader = new EntityCollectionLoader(connection, entityMetaRegistry);
    }

    @Override
    public <T> T load(Class<T> clazz, Object id) {
        final EntityClassMappingMeta entityClassMappingMeta = entityMetaRegistry.getEntityMeta(clazz);

        final SelectStatementBuilder selectStatementBuilder = SelectStatementBuilder.builder()
            .selectFrom(entityClassMappingMeta);

        final String selectSql = selectStatementBuilder
            .where(WherePredicate.of(entityClassMappingMeta.getIdFieldColumnName(), id, new EqualOperator()))
            .build();

        final T queryObject = jdbcTemplate.queryForObject(selectSql, new EntityRowMapper<>(clazz, entityMetaRegistry));

        if (entityClassMappingMeta.hasNoRelation()) {
            return queryObject;
        }

        return entityCollectionLoader.loadCollection(clazz, queryObject);
    }
}
