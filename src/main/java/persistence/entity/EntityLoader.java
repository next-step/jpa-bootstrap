package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.meta.Metamodel;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;
import persistence.sql.dml.query.SelectQueryBuilder;

public class EntityLoader {
    private final JdbcTemplate jdbcTemplate;
    private final Metamodel metamodel;

    public EntityLoader(JdbcTemplate jdbcTemplate, Metamodel metamodel) {
        this.jdbcTemplate = jdbcTemplate;
        this.metamodel = metamodel;
    }

    public <T> T loadEntity(Class<T> entityClass, EntityKey entityKey) {
        final SelectQueryBuilder queryBuilder = new SelectQueryBuilder(entityKey.entityClass(), metamodel);
        final TableDefinition tableDefinition = metamodel.getTableDefinition(entityClass);

        tableDefinition.getAssociations().stream()
                .filter(TableAssociationDefinition::isEager)
                .forEach(association ->
                        queryBuilder.join(metamodel.getTableDefinition(association.getAssociatedEntityClass()))
                );

        final String query = queryBuilder.buildById(entityKey.id());
        final Object queried = jdbcTemplate.queryForObject(query, metamodel.getRowMapper(entityClass));
        return entityClass.cast(queried);
    }
}
