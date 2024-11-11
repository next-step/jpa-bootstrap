package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperFactory;
import persistence.meta.Metamodel;
import persistence.sql.definition.TableDefinition;
import persistence.sql.dml.query.SelectQueryBuilder;

public class EntityLoader {
    private final TableDefinition tableDefinition;
    private final JdbcTemplate jdbcTemplate;
    private final Metamodel metamodel;

    public EntityLoader(TableDefinition tableDefinition, JdbcTemplate jdbcTemplate, Metamodel metamodel) {
        this.jdbcTemplate = jdbcTemplate;
        this.tableDefinition = tableDefinition;
        this.metamodel = metamodel;
    }

    public <T> T loadEntity(Class<T> entityClass, EntityKey entityKey) {
        final SelectQueryBuilder queryBuilder = new SelectQueryBuilder(entityKey.entityClass(), metamodel);
        tableDefinition.resolveEagerAssociation()
                .forEach(association ->
                        queryBuilder.join(
                                tableDefinition.getJoinColumnName(association.getAssociatedEntityClass()),
                                metamodel.findEntityPersister(association.getAssociatedEntityClass())
                        )
                );

        final String query = queryBuilder.buildById(entityKey.id());
        final Object queried = jdbcTemplate.queryForObject(query,
                RowMapperFactory.getInstance().getRowMapper(entityClass, metamodel, jdbcTemplate));

        return entityClass.cast(queried);
    }
}
