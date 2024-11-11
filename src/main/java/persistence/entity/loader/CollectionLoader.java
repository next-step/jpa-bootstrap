package persistence.entity.loader;

import jdbc.JdbcTemplate;
import jdbc.mapper.RowMapper;
import persistence.meta.AssociationCondition;
import persistence.meta.EntityTable;
import persistence.sql.dml.SelectQuery;

import java.util.List;

public class CollectionLoader {
    private final EntityTable entityTable;
    private final JdbcTemplate jdbcTemplate;
    private final SelectQuery selectQuery;
    private final RowMapper rowMapper;

    public CollectionLoader(EntityTable entityTable, JdbcTemplate jdbcTemplate, SelectQuery selectQuery, RowMapper rowMapper) {
        this.entityTable = entityTable;
        this.jdbcTemplate = jdbcTemplate;
        this.selectQuery = selectQuery;
        this.rowMapper = rowMapper;
    }

    public List<?> load(AssociationCondition associationCondition) {
        final String sql = selectQuery.findCollection(
                entityTable, associationCondition.getColumnName(), associationCondition.getId());
        return jdbcTemplate.query(sql, rowMapper);
    }
}
