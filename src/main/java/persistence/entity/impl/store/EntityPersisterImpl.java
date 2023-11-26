package persistence.entity.impl.store;

import java.sql.Connection;
import jdbc.JdbcTemplate;
import persistence.entity.impl.EntityRowMapper;
import persistence.sql.dml.clause.operator.EqualOperator;
import persistence.sql.dml.clause.predicate.WherePredicate;
import persistence.sql.dml.statement.DeleteStatementBuilder;
import persistence.sql.dml.statement.InsertStatementBuilder;
import persistence.sql.dml.statement.UpdateStatementBuilder;
import persistence.sql.schema.meta.EntityClassMappingMeta;
import persistence.sql.schema.meta.EntityObjectMappingMeta;
import registry.EntityMetaRegistry;

public class EntityPersisterImpl implements EntityPersister {

    private final JdbcTemplate jdbcTemplate;
    private final EntityMetaRegistry entityMetaRegistry;

    public EntityPersisterImpl(Connection connection, EntityMetaRegistry entityMetaRegistry) {
        this.jdbcTemplate = new JdbcTemplate(connection);
        this.entityMetaRegistry = entityMetaRegistry;
    }

    @Override
    public Object store(Object entity) {
        final InsertStatementBuilder insertStatementBuilder = new InsertStatementBuilder();
        final String insertSql = insertStatementBuilder.insertReturning(entity, entityMetaRegistry.getEntityMeta(entity.getClass()));
        return jdbcTemplate.executeReturning(insertSql, new EntityRowMapper<>(entity.getClass(), entityMetaRegistry));
    }

    @Override
    public void update(Object entity) {
        final String updateSql = UpdateStatementBuilder.builder()
            .update(entity, entityMetaRegistry.getEntityMeta(entity.getClass()))
            .equalById()
            .build();

        jdbcTemplate.executeUpdate(updateSql);
    }

    @Override
    public void delete(Object entity) {
        final EntityClassMappingMeta entityClassMappingMeta = entityMetaRegistry.getEntityMeta(entity.getClass());

        final EntityObjectMappingMeta objectMappingMeta = EntityObjectMappingMeta.of(entity, entityClassMappingMeta);

        final String deleteSql = DeleteStatementBuilder.builder()
            .delete(entityClassMappingMeta)
            .where(WherePredicate.of(objectMappingMeta.getIdColumnName(), objectMappingMeta.getIdValue(), new EqualOperator()))
            .build();
        jdbcTemplate.execute(deleteSql);
    }


}
