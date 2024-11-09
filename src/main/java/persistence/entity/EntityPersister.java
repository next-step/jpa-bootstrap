package persistence.entity;

import jdbc.DefaultIdMapper;
import jdbc.JdbcTemplate;
import persistence.meta.EntityColumn;
import persistence.sql.dml.DeleteQuery;
import persistence.sql.dml.InsertQuery;
import persistence.sql.dml.UpdateQuery;

import java.util.List;

public class EntityPersister {
    private final JdbcTemplate jdbcTemplate;
    private final InsertQuery insertQuery;
    private final UpdateQuery updateQuery;
    private final DeleteQuery deleteQuery;

    public EntityPersister(JdbcTemplate jdbcTemplate, InsertQuery insertQuery,
                           UpdateQuery updateQuery, DeleteQuery deleteQuery) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertQuery = insertQuery;
        this.updateQuery = updateQuery;
        this.deleteQuery = deleteQuery;
    }

    public void insert(Object entity) {
        final String sql = insertQuery.insert(entity);
        jdbcTemplate.executeAndReturnGeneratedKeys(sql, new DefaultIdMapper(entity));
    }

    public void update(Object entity, List<EntityColumn> entityColumns) {
        jdbcTemplate.execute(updateQuery.update(entity, entityColumns));
    }

    public void delete(Object entity) {
        jdbcTemplate.execute(deleteQuery.delete(entity));
    }
}
