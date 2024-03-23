package persistence.entity.database;

import database.dialect.Dialect;
import database.mapping.rowmapper.SingleRowMapperFactory;
import database.sql.dml.Select;
import database.sql.dml.SelectByPrimaryKey;
import database.sql.dml.part.WhereMap;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import persistence.entity.context.PersistentClass;

import java.util.List;
import java.util.Optional;

public class EntityLoader<T> {
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;
    private final PersistentClass<T> persistentClass;
    private List<Class<?>> entities;

    public EntityLoader(PersistentClass<T> persistentClass, JdbcTemplate jdbcTemplate,
                        Dialect dialect, List<Class<?>> entities) {
        this.persistentClass = persistentClass;
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;

        this.entities = entities;
    }

    public Optional<T> load(Long id) {
        RowMapper<T> rowMapper = SingleRowMapperFactory.create(persistentClass, dialect);

        String query = SelectByPrimaryKey.from(persistentClass, entities)
                .byId(id)
                .buildQuery();
        return jdbcTemplate.query(query, rowMapper).stream().findFirst();
    }

    public List<T> load(WhereMap whereMap) {
        RowMapper<T> rowMapper = SingleRowMapperFactory.create(persistentClass, dialect);

        String query = Select.from(persistentClass, entities)
                .where(whereMap)
                .buildQuery();
        return jdbcTemplate.query(query, rowMapper);
    }
}
