package persistence.entity.database;

import database.mapping.rowmapper.JoinedRow;
import database.mapping.rowmapper.JoinedRowsCombiner;
import database.sql.dml.CustomSelect;
import database.sql.dml.part.WhereMap;
import jdbc.JdbcTemplate;
import persistence.bootstrap.Metadata;
import persistence.bootstrap.Metamodel;
import persistence.entity.context.PersistentClass;

import java.util.List;
import java.util.Optional;

public class CollectionLoader<T> {
    private final PersistentClass<T> persistentClass;
    private final Metadata metadata;
    private final JdbcTemplate jdbcTemplate;
    private final Metamodel metamodel;

    public CollectionLoader(PersistentClass<T> persistentClass, Metadata metadata, JdbcTemplate jdbcTemplate,
                            Metamodel metamodel) {
        this.persistentClass = persistentClass;
        this.metadata = metadata;
        this.jdbcTemplate = jdbcTemplate;
        this.metamodel = metamodel;
    }

    public Optional<T> load(Long id) {
        String query = CustomSelect.from(persistentClass, metadata).buildQuery(WhereMap.of("id", id));
        List<JoinedRow<T>> joinedRows = jdbcTemplate.query(query, persistentClass.getJoinedRowMapper());

        return new JoinedRowsCombiner<>(joinedRows, persistentClass, metadata, metamodel).merge();
    }
}
