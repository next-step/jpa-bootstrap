package persistence.entity.database;

import database.dialect.Dialect;
import database.mapping.Association;
import database.mapping.rowmapper.JoinedRow;
import database.mapping.rowmapper.JoinedRowMapper;
import database.mapping.rowmapper.JoinedRowsCombiner;
import database.sql.dml.CustomSelect;
import database.sql.dml.part.WhereMap;
import jdbc.JdbcTemplate;
import persistence.entity.context.PersistentClass;

import java.util.List;
import java.util.Optional;

public class CollectionLoader<T> {
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;
    private final PersistentClass<T> persistentClass;
    private final List<Class<?>> entities;

    public CollectionLoader(PersistentClass<T> persistentClass, JdbcTemplate jdbcTemplate, Dialect dialect,
                            List<Class<?>> entities) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
        this.persistentClass = persistentClass;

        this.entities = entities;
    }

    public Optional<T> load(Long id) {
        String query = CustomSelect.from(persistentClass, entities).buildQuery(WhereMap.of("id", id));
        JoinedRowMapper<T> rowMapper = new JoinedRowMapper<>(persistentClass, dialect);
        List<JoinedRow<T>> joinedRows = jdbcTemplate.query(query, rowMapper);

        // TODO: 조만간 clazz 대신 가져올 객체 통해서 얻을 수 있을거라고 믿고 여기 둠
        List<Association> associations = getAssociations();

        return new JoinedRowsCombiner<>(joinedRows, persistentClass, associations, jdbcTemplate, dialect, entities).merge();
    }

    private List<Association> getAssociations() {
        return persistentClass.getAssociations();
    }
}
