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
        final String sql = insertQuery.insert(entityTable, entity);
        jdbcTemplate.executeAndReturnGeneratedKeys(sql, new DefaultIdMapper(entity));
    }

    public void update(Object updatedEntity, List<EntityColumn> entityColumns) {
        final String sql = updateQuery.update(entityTable, entityColumns, updatedEntity);
        jdbcTemplate.execute(sql);
    }

    public void delete(Object entity) {
        final String sql = deleteQuery.delete(entityTable, entity);
        jdbcTemplate.execute(sql);
    }
}
