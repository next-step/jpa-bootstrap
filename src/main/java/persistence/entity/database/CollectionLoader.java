package persistence.entity.database;

import database.dialect.Dialect;
import database.mapping.rowmapper.JoinedRow;
import database.mapping.rowmapper.JoinedRowMapper;
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
    private final JdbcTemplate jdbcTemplate;
    private final CustomSelect customSelectQuery;
    private final JoinedRowMapper<T> joinedRowMapper;
    private final JoinedRowsCombiner<T> joinedRowsCombiner;

    public static <T> CollectionLoader<T> from(PersistentClass<T> persistentClass,
                                               Metadata metadata,
                                               JdbcTemplate jdbcTemplate,
                                               Dialect dialect,
                                               Metamodel metamodel) {
        return new CollectionLoader<>(jdbcTemplate,
                                      CustomSelect.from(persistentClass, metadata),
                                      new JoinedRowMapper<>(persistentClass, dialect),
                                      new JoinedRowsCombiner<>(persistentClass, metadata, metamodel));
    }

    private CollectionLoader(JdbcTemplate jdbcTemplate, CustomSelect customSelectQuery,
                             JoinedRowMapper<T> joinedRowMapper, JoinedRowsCombiner<T> joinedRowsCombiner) {
        this.jdbcTemplate = jdbcTemplate;
        this.customSelectQuery = customSelectQuery;
        this.joinedRowMapper = joinedRowMapper;
        this.joinedRowsCombiner = joinedRowsCombiner;
    }

    public Optional<T> load(Long id) {
        String query = customSelectQuery.toSql(WhereMap.of("id", id));
        List<JoinedRow<T>> joinedRows = jdbcTemplate.query(query, joinedRowMapper);
        return joinedRowsCombiner.merge(joinedRows);
    }
}
