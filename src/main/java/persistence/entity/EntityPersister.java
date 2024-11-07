package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;
import persistence.sql.dml.query.DeleteQueryBuilder;
import persistence.sql.dml.query.UpdateQueryBuilder;

import java.io.Serializable;
import java.util.List;

public class EntityPersister {
    private static final Long DEFAULT_ID_VALUE = 0L;

    private static final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
    private static final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();

    private final InsertExecutor insertExecutor;
    private final TableDefinition tableDefinition;

    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(TableDefinition tableDefinition, JdbcTemplate jdbcTemplate) {
        this.tableDefinition = tableDefinition;
        this.jdbcTemplate = jdbcTemplate;
        this.insertExecutor = new InsertExecutor(jdbcTemplate, tableDefinition);
    }

    public boolean hasId(Object entity) {
        return tableDefinition.hasId(entity);
    }

    public Serializable getEntityId(Object entity) {
        if (tableDefinition.hasId(entity)) {
            return tableDefinition.getIdValue(entity);
        }

        return DEFAULT_ID_VALUE;
    }

    public Object insert(Object entity) {
        return insertExecutor.insertAndBindKey(entity, tableDefinition);
    }

    public List<TableAssociationDefinition> getCollectionAssociations() {
        return tableDefinition.getAssociations().stream().filter(
                TableAssociationDefinition::isCollection
        ).toList();
    }

    public void update(Object entity) {
        final String query = updateQueryBuilder.build(entity, tableDefinition);
        jdbcTemplate.execute(query);
    }

    public void delete(Object entity) {
        String query = deleteQueryBuilder.build(entity, tableDefinition);
        jdbcTemplate.execute(query);
    }

}
