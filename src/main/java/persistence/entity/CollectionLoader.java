package persistence.entity;

import jdbc.DefaultRowMapper;
import jdbc.JdbcTemplate;
import persistence.meta.AssociationCondition;
import persistence.meta.EntityTable;
import persistence.sql.dml.SelectQuery;

import java.util.List;

public class CollectionLoader {
    private final EntityTable entityTable;
    private final JdbcTemplate jdbcTemplate;
    private final SelectQuery selectQuery;

    public CollectionLoader(EntityTable entityTable, JdbcTemplate jdbcTemplate, SelectQuery selectQuery) {
        this.entityTable = entityTable;
        this.jdbcTemplate = jdbcTemplate;
        this.selectQuery = selectQuery;
    }

    public List<?> load(AssociationCondition associationCondition) {
        final String sql = selectQuery.findCollection(
                entityTable, associationCondition.getColumnName(), associationCondition.getId());
        return jdbcTemplate.query(sql, new DefaultRowMapper<>(entityTable.getType()));
    }
}
