package persistence.entity.database;

import database.dialect.Dialect;
import database.mapping.Association;
import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import database.mapping.rowmapper.JoinedRow;
import database.mapping.rowmapper.JoinedRowMapper;
import database.mapping.rowmapper.JoinedRowsCombiner;
import database.sql.dml.CustomSelect;
import database.sql.dml.part.WhereMap;
import jdbc.JdbcTemplate;

import java.util.List;
import java.util.Optional;

public class CollectionLoader<T> {
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;
    private final Class<T> clazz;
    private final EntityMetadata entityMetadata;

    public CollectionLoader(Class<T> clazz, JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
        this.clazz = clazz;
        this.entityMetadata = EntityMetadataFactory.get(clazz);
    }

    public Optional<T> load(Long id) {
        String query = new CustomSelect(clazz).buildQuery(WhereMap.of("id", id));
        JoinedRowMapper<T> rowMapper = new JoinedRowMapper<>(clazz, dialect);
        List<JoinedRow<T>> joinedRows = jdbcTemplate.query(query, rowMapper);

        // TODO: 조만간 clazz 대신 가져올 객체 통해서 얻을 수 있을거라고 믿고 여기 둠
        List<Association> associations = getAssociations();

        return new JoinedRowsCombiner<>(joinedRows, clazz, associations, jdbcTemplate, dialect).merge();
    }

    private List<Association> getAssociations() {
        return entityMetadata.getAssociations();
    }
}
