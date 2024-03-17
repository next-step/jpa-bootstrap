package persistence.entity.loader;

import java.util.List;
import java.util.Map;
import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlGenerator;
import persistence.sql.meta.Column;

public class SimpleEntityLoader<T> implements EntityLoader<T> {

    private final JdbcTemplate jdbcTemplate;
    private final DmlGenerator dmlGenerator;
    private final Class<T> type;

    private SimpleEntityLoader(JdbcTemplate jdbcTemplate, Class<T> type) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlGenerator = DmlGenerator.getInstance();
        this.type = type;
    }

    public static <T> SimpleEntityLoader<T> of(JdbcTemplate jdbcTemplate, Class<T> type) {
        return new SimpleEntityLoader<>(jdbcTemplate, type);
    }

    @Override
    public T find(Long id) {
       return jdbcTemplate.queryForObject(dmlGenerator.generateSelectQuery(type, id),
            resultSet -> new EntityRowMapper<>(type).mapRow(resultSet));
    }

    @Override
    public List<T> find(Map<Column, Object> conditions) {
        return jdbcTemplate.query(dmlGenerator.generateSelectQuery(type, conditions),
            resultSet -> new EntityRowMapper<>(type).mapRow(resultSet));
    }
}
