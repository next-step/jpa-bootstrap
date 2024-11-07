package persistence.entity;

import common.ReflectionFieldAccessUtils;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.sql.definition.TableDefinition;
import persistence.sql.dml.query.InsertQueryBuilder;

import java.io.Serializable;
import java.lang.reflect.Field;

public class InsertExecutor {

    private static final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;
    private final TableDefinition tableDefinition;

    public InsertExecutor(JdbcTemplate jdbcTemplate, TableDefinition tableDefinition) {
        this.jdbcTemplate = jdbcTemplate;
        this.tableDefinition = tableDefinition;
    }

    public Object insertAndBindKey(Object entity, TableDefinition tableDefinition) {
        final String query = insertQueryBuilder.build(entity, tableDefinition);
        final Serializable id = jdbcTemplate.insertAndReturnKey(query);

        bindId(id, entity, tableDefinition);
        return entity;
    }

    private void bindId(Serializable id, Object entity, TableDefinition tableDefinition) {
        try {
            final Field idField = tableDefinition.getEntityClass()
                    .getDeclaredField(tableDefinition.getIdFieldName());

            ReflectionFieldAccessUtils.accessAndSet(entity, idField, id);
        } catch (ReflectiveOperationException e) {
            logger.error("Failed to copy row to {}", entity.getClass().getName(), e);
        }
    }
}
