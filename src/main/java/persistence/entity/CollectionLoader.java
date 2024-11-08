package persistence.entity;

import jdbc.DefaultRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dml.SelectQuery;

import java.util.List;

public class CollectionLoader {
    private final JdbcTemplate jdbcTemplate;
    private final SelectQuery selectQuery;

    public CollectionLoader(JdbcTemplate jdbcTemplate, SelectQuery selectQuery) {
        this.jdbcTemplate = jdbcTemplate;
        this.selectQuery = selectQuery;
    }

    public <T> List<T> load(Class<T> entityType, String columnName, Object value) {
        final String sql = selectQuery.findCollection(entityType, columnName, value);
        return jdbcTemplate.query(sql, new DefaultRowMapper<>(entityType));
    }
}
