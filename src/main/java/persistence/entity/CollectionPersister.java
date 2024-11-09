package persistence.entity;

import jdbc.DefaultIdMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dml.InsertQuery;

import java.util.List;

public class CollectionPersister {
    private final JdbcTemplate jdbcTemplate;
    private final InsertQuery insertQuery;

    public CollectionPersister(JdbcTemplate jdbcTemplate, InsertQuery insertQuery) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertQuery = insertQuery;
    }

    public <T> void insert(List<T> collection, Object parentEntity) {
        for (T entity : collection) {
            final String sql = insertQuery.insert(entity, parentEntity);
            jdbcTemplate.executeAndReturnGeneratedKeys(sql, new DefaultIdMapper(entity));
        }
    }
}
