package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;
import persistence.sql.meta.IdColumn;
import persistence.sql.meta.Table;

public class MyEntityPersister<T> implements EntityPersister<T> {

    private final JdbcTemplate jdbcTemplate;
    private final EntityMeta<T> entityMeta;
    private final InsertQueryBuilder insertQueryBuilder;
    private final UpdateQueryBuilder updateQueryBuilder;
    private final DeleteQueryBuilder deleteQueryBuilder;

    public MyEntityPersister(JdbcTemplate jdbcTemplate, EntityMeta<T> entityMeta) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMeta = entityMeta;
        this.insertQueryBuilder = InsertQueryBuilder.getInstance();
        this.updateQueryBuilder = UpdateQueryBuilder.getInstance();
        this.deleteQueryBuilder = DeleteQueryBuilder.getInstance();
    }

    @Override
    public boolean update(Object entity) {
        Table table = entityMeta.getTable();
        IdColumn idColumn = entityMeta.getIdColumn();
        String query = updateQueryBuilder.build(entity, table, idColumn);
        return jdbcTemplate.executeForUpdate(query);
    }

    @Override
    public Object insert(Object entity) {
        Table table = entityMeta.getTable();
        String query = insertQueryBuilder.build(entity, table);
        return jdbcTemplate.executeForInsert(query);
    }

    @Override
    public void delete(Object entity) {
        Table table = entityMeta.getTable();
        IdColumn idColumn = entityMeta.getIdColumn();
        Object id = entityMeta.getId(entity);
        String query = deleteQueryBuilder.build(table, idColumn, id);
        jdbcTemplate.execute(query);
    }
}
