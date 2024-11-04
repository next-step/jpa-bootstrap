package persistence.entity;

import common.ReflectionFieldAccessUtils;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;
import persistence.sql.dml.query.DeleteQueryBuilder;
import persistence.sql.dml.query.InsertQueryBuilder;
import persistence.sql.dml.query.UpdateQueryBuilder;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

public class EntityPersister {
    private static final Long DEFAULT_ID_VALUE = 0L;

    private static final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
    private static final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
    private static final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();

    private final Logger logger = LoggerFactory.getLogger(EntityPersister.class);
    private final TableDefinition tableDefinition;

    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(TableDefinition tableDefinition, JdbcTemplate jdbcTemplate) {
        this.tableDefinition = tableDefinition;
        this.jdbcTemplate = jdbcTemplate;
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
        final String query = insertQueryBuilder.build(entity, tableDefinition);
        final Serializable id = jdbcTemplate.insertAndReturnKey(query);

        bindId(id, entity);
        return entity;
    }

    public List<TableAssociationDefinition> getCollectionAssociations() {
        return tableDefinition.getAssociations().stream().filter(
                TableAssociationDefinition::isCollection
        ).toList();
    }

    private void bindId(Serializable id, Object entity) {
        try {
            final Field idField = tableDefinition.getEntityClass()
                    .getDeclaredField(tableDefinition.getIdFieldName());

            ReflectionFieldAccessUtils.accessAndSet(entity, idField, id);
        } catch (ReflectiveOperationException e) {
            logger.error("Failed to copy row to {}", entity.getClass().getName(), e);
        }
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
