package persistence.entity.persister;

import jdbc.JdbcTemplate;
import jdbc.mapper.DefaultIdMapper;
import persistence.meta.EntityColumn;
import persistence.meta.EntityTable;
import persistence.sql.dml.DeleteQuery;
import persistence.sql.dml.InsertQuery;
import persistence.sql.dml.UpdateQuery;

import java.util.List;

public class EntityPersister {
    private final EntityTable entityTable;
    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(EntityTable entityTable, JdbcTemplate jdbcTemplate) {
        this.entityTable = entityTable;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(Object entity) {
        final InsertQuery insertQuery = InsertQuery.getInstance();
        final String sql = insertQuery.insert(entityTable, entity);
        jdbcTemplate.executeAndReturnGeneratedKeys(sql, new DefaultIdMapper(entity));
    }

    public void update(Object updatedEntity, List<EntityColumn> entityColumns) {
        final UpdateQuery updateQuery = UpdateQuery.getInstance();
        final String sql = updateQuery.update(entityTable, entityColumns, updatedEntity);
        jdbcTemplate.execute(sql);
    }

    public void delete(Object entity) {
        final DeleteQuery deleteQuery = DeleteQuery.getInstance();
        final String sql = deleteQuery.delete(entityTable, entity);
        jdbcTemplate.execute(sql);
    }
}
