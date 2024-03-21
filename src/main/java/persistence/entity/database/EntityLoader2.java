package persistence.entity.database;

import database.dialect.Dialect;
import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import database.mapping.rowmapper.SingleRowMapperFactory;
import database.sql.dml.Select;
import database.sql.dml.SelectByPrimaryKey;
import database.sql.dml.part.WhereMap;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;

import java.util.List;
import java.util.Optional;

public class EntityLoader2<T> {
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;
    private final Class<T> clazz;

    public EntityLoader2(Class<T> clazz, JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;

        this.clazz = clazz;
    }

    public Optional<T> load(Long id) {
        RowMapper<T> rowMapper = SingleRowMapperFactory.create(clazz, dialect);

        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);
        String query = new SelectByPrimaryKey(entityMetadata.getTableName(),
                                              entityMetadata.getAllColumnNamesWithAssociations(),
                                              entityMetadata.getPrimaryKeyName(),
                                              entityMetadata.getGeneralColumnNames())
                .byId(id)
                .buildQuery();
        return jdbcTemplate.query(query, rowMapper).stream().findFirst();
    }

    public List<T> load(WhereMap whereMap) {
        RowMapper<T> rowMapper = SingleRowMapperFactory.create(clazz, dialect);

        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);
        String query = new Select(entityMetadata.getTableName(),
                                  entityMetadata.getAllColumnNamesWithAssociations(),
                                  entityMetadata.getPrimaryKeyName(),
                                  entityMetadata.getGeneralColumnNames())
                .where(whereMap)
                .buildQuery();
        return jdbcTemplate.query(query, rowMapper);
    }
}
