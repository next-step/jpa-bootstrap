package persistence.entity.persister;

import jdbc.JdbcTemplate;
import jdbc.mapper.DefaultIdMapper;
import persistence.meta.EntityTable;
import persistence.sql.dml.InsertQuery;

import java.util.List;

public class CollectionPersister {
    private final EntityTable entityTable;
    private final EntityTable parentEntityTable;
    private final JdbcTemplate jdbcTemplate;

    public CollectionPersister(
            EntityTable entityTable, EntityTable parentEntityTable, JdbcTemplate jdbcTemplate) {
        this.entityTable = entityTable;
        this.parentEntityTable = parentEntityTable;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(List<?> collection, Object parentEntity) {
        final InsertQuery insertQuery = InsertQuery.getInstance();
        for (Object entity : collection) {
            final String sql =
                    insertQuery.insert(entityTable, parentEntityTable.getAssociationColumnName(), parentEntityTable.getIdValue(parentEntity), entity);
            jdbcTemplate.executeAndReturnGeneratedKeys(sql, new DefaultIdMapper(entity));
        }
    }
}
