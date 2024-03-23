package persistence.entity.database;

import database.sql.dml.Select;
import database.sql.dml.SelectByPrimaryKey;
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
    private final Metadata metadata;

    public EntityLoader(PersistentClass<T> persistentClass, Metadata metadata, JdbcTemplate jdbcTemplate) {
        this.persistentClass = persistentClass;
        this.metadata = metadata;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<T> load(Long id) {
        String query = SelectByPrimaryKey.from(persistentClass, metadata)
                .byId(id)
                .buildQuery();
        return jdbcTemplate.query(query, persistentClass.getRowMapper()).stream().findFirst();
    }

    public List<T> load(WhereMap whereMap) {
        return select(whereMap, persistentClass.getRowMapper());
    }

    private List<T> select(WhereMap whereMap, RowMapper<T> rowMapper) {
        String query = Select.from(persistentClass, metadata)
                .where(whereMap)
                .buildQuery();
        return jdbcTemplate.query(query, rowMapper);
    }
}
