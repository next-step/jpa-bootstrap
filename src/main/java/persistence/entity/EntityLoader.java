package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperFactory;
import persistence.meta.Metamodel;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;
import persistence.sql.dml.query.SelectQueryBuilder;

public class EntityLoader {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapperFactory rowMapperFactory;
    private final Metamodel metamodel;

    public EntityLoader(JdbcTemplate jdbcTemplate, Metamodel metamodel) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapperFactory = RowMapperFactory.getInstance();
        this.metamodel = metamodel;
    }

    public <T> T loadEntity(Class<T> entityClass, EntityKey entityKey) {
        final SelectQueryBuilder queryBuilder = new SelectQueryBuilder(entityKey.entityClass(), metamodel);
        final TableDefinition tableDefinition = new TableDefinition(entityKey.entityClass());

        tableDefinition.getAssociations().stream()
                .filter(TableAssociationDefinition::isEager)
                .forEach(queryBuilder::join);

        final String query = queryBuilder.buildById(entityKey.id());

        final Object queried = jdbcTemplate.queryForObject(query,
                rowMapperFactory.createRowMapper(entityClass, metamodel, jdbcTemplate)
        );

        return entityClass.cast(queried);
    }
}
