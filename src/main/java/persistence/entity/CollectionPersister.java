package persistence.entity;

import jdbc.DefaultIdMapper;
import jdbc.JdbcTemplate;
import persistence.meta.EntityTable;
import persistence.sql.dml.InsertQuery;

import java.util.List;

public class CollectionPersister {
    private final EntityTable entityTable;
    private final EntityTable parentEntityTable;
    private final JdbcTemplate jdbcTemplate;
    private final InsertQuery insertQuery;

    public CollectionPersister(
            EntityTable entityTable, EntityTable parentEntityTable, JdbcTemplate jdbcTemplate, InsertQuery insertQuery) {
        this.entityTable = entityTable;
        this.parentEntityTable = parentEntityTable;
        this.jdbcTemplate = jdbcTemplate;
        this.insertQuery = insertQuery;
    }

    public void insert(List<?> collection, Object parentEntity) {
        for (Object entity : collection) {
            final String sql = insertQuery.insert(
                    entityTable, parentEntityTable.getAssociationColumnName(), parentEntityTable.getIdValue(parentEntity), entity);
            jdbcTemplate.executeAndReturnGeneratedKeys(sql, new DefaultIdMapper(entity));
        }
    }
}
