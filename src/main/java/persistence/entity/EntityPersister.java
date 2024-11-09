package persistence.entity;

import jdbc.DefaultIdMapper;
import jdbc.JdbcTemplate;
import persistence.meta.EntityColumn;
import persistence.meta.EntityTable;
import persistence.sql.dml.DeleteQuery;
import persistence.sql.dml.InsertQuery;
import persistence.sql.dml.UpdateQuery;

import java.util.List;

public class EntityPersister {
    private final EntityTable entityTable;
    private final JdbcTemplate jdbcTemplate;
    private final InsertQuery insertQuery;
    private final UpdateQuery updateQuery;
    private final DeleteQuery deleteQuery;

    public EntityPersister(EntityTable entityTable, JdbcTemplate jdbcTemplate, InsertQuery insertQuery,
                           UpdateQuery updateQuery, DeleteQuery deleteQuery) {
        this.entityTable = entityTable;
        this.jdbcTemplate = jdbcTemplate;
        this.insertQuery = insertQuery;
        this.updateQuery = updateQuery;
        this.deleteQuery = deleteQuery;
    }

    public void insert(Object entity) {
        entityTable.setValue(entity);
        final String sql = insertQuery.insert(entityTable);
        jdbcTemplate.executeAndReturnGeneratedKeys(sql, new DefaultIdMapper(entity));
    }

    public void update(Object entity, List<EntityColumn> entityColumns) {
        entityTable.setValue(entity);
        jdbcTemplate.execute(updateQuery.update(entityTable, entityColumns));
    }

    public void delete(Object entity) {
        entityTable.setValue(entity);
        jdbcTemplate.execute(deleteQuery.delete(entityTable));
    }
}
