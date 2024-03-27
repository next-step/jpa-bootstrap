package persistence.entity.database;

import database.sql.dml.Select;
import database.sql.dml.part.WhereMap;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import persistence.bootstrap.Metadata;
import persistence.entity.context.PersistentClass;

import java.util.List;
import java.util.Optional;

public class EntityLoader<T> {
    private final JdbcTemplate jdbcTemplate;
    private final PersistentClass<T> persistentClass;
    private final Select selectQuery;

    public static <T> EntityLoader<T> from(PersistentClass<T> persistentClass,
                                           Metadata metadata,
                                           JdbcTemplate jdbcTemplate) {
        return new EntityLoader<>(persistentClass,
                                  jdbcTemplate,
                                  Select.from(persistentClass, metadata));
    }

    private EntityLoader(PersistentClass<T> persistentClass, JdbcTemplate jdbcTemplate, Select selectQuery) {
        this.persistentClass = persistentClass;
        this.jdbcTemplate = jdbcTemplate;
        this.selectQuery = selectQuery;
    }

    public Optional<T> load(Long id) {
        String query = selectQuery.toSql(id);
        return jdbcTemplate.query(query, persistentClass.getRowMapper()).stream().findFirst();
    }

    public List<T> load(WhereMap whereMap) {
        return select(whereMap, persistentClass.getRowMapper());
    }

    private List<T> select(WhereMap whereMap, RowMapper<T> rowMapper) {
        String query = selectQuery.toSql(whereMap);
        return jdbcTemplate.query(query, rowMapper);
    }
}
